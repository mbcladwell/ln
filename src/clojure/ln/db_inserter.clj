(ns ln.db-inserter
  (:require [next.jdbc :as j]
            [next.jdbc.prepare :as p]
            [next.jdbc.result-set :as rs]
            [next.jdbc.protocols :as proto]
            [clojure.set :as s]
           ;; [honeysql.core :as hsql]
            [incanter.stats :as is]
           ;; [honeysql.helpers :refer :all :as helpers]
            [clojure.string :only [split split-lines trim]] 
            [ln.codax-manager :as cm]
            [ln.db-manager :as dbm])
  (:import [java.sql.DriverManager] [javax.swing.JOptionPane]))

(defn tokens
  [s]
  (-> s clojure.string/trim (clojure.string/split #"\s+")))

(defn pairs
  [coll1 coll2]
  (map vector coll1 coll2))

(defn parse-table
  [raw-table-data]
  (let [table-data (map tokens (clojure.string/split-lines raw-table-data))
        column-keys (map keyword (first table-data))
        contents  (next table-data)]
    (for [x contents]
  (into (sorted-map)(pairs column-keys x)))))

(defn table-to-map [ file]
  (->
    file
    slurp
    parse-table))

(defn get-col-names [ file ]
 (first (map tokens (clojure.string/split-lines (slurp file)))))


(defn import-barcode-ids [ plateset-id barcode-file]
   " Loads table and make the association
      barcodes looks like:

      plate 	barcode.id
      1     	AMRVK5473H
      1      	KMNCX9294W
      1      	EHRXZ2102Z
      1      	COZHR7852Q
      1      	FJVNR6433Q"    
  (let [ col1name (first (get-col-names barcode-file))
        col2name (first (rest (get-col-names barcode-file)))
        table (table-to-map barcode-file)
        sql-statement (str "UPDATE plate SET barcode = ? WHERE plate.ID IN ( SELECT plate.id FROM plate_set, plate_plate_set, plate  WHERE plate_plate_set.plate_set_id=" (str plateset-id) " AND plate_plate_set.plate_id=plate.id AND plate_plate_set.plate_order=? )")
        content (into [] (zipmap (map #(:barcode.id %) table) (map #(Integer. (:plate %)) table)))
        ]
    (if (and (= col1name "plate")(= col2name "barcode.id"))
      (with-open [con (j/get-connection dbm/pg-db)
                  ps  (j/prepare con [sql-statement])]
        (p/execute-batch! ps content))    
      (javax.swing.JOptionPane/showMessageDialog nil  (str "Expecting the headers \"plate\", and \"barcode.id\", but found\n" col1name  ", and " col2name  "."  )))))

;; Diagnostic select:  select plate.id, plate.barcode, plate.plate_sys_name from plate, plate_set, plate_plate_set where plate_plate_set.plate_id=plate.id and plate_plate_set.plate_set_id=plate_set.id and plate_set.id=7 order by plate.barcode; 



(defn process-accs-map
;;order is important; must correlate with SQL statement order of ?'s
  [x]
(into [] [(:accs.id x ) (Integer/parseInt(:plate x)) (Integer/parseInt(:well x ))]))


(defn import-accession-ids 
  " Loads table and make the association accessions looks like:
 plate	well	accs.id
1	1	AMRVK5473H
1	2	KMNCX9294W
1	3	EHRXZ2102Z
1	4	COZHR7852Q
1	5	FJVNR6433Q
1	6	WTCKQ4682U"
  
[ plateset-id accession-file]
  (let [ col1name (first (get-col-names accession-file))
        col2name (second (get-col-names accession-file))
        col3name (nth (get-col-names accession-file) 2)        
        table (table-to-map accession-file)
        content (into [] (map #(process-accs-map %) table))
        sql-statement (str "UPDATE sample SET accs_id = ? WHERE sample.ID IN ( SELECT sample.id FROM plate_set, plate_plate_set, plate, well, well_sample, sample WHERE plate_plate_set.plate_set_id=" (str plateset-id)   " AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID AND plate_plate_set.plate_order=? AND well.by_col=?)")
        ]
    (if (and (= col3name "accs.id")(= col1name "plate")(= col2name "well"))
      (with-open [con (j/get-connection dbm/pg-db)
                  ps  (j/prepare con [sql-statement])]
        (p/execute-batch! ps content))    
      (javax.swing.JOptionPane/showMessageDialog nil  (str "Expecting the headers \"plate\", \"well\", and \"accs.id\", but found\n" col1name ", " col2name  ", and " col3name  "."  )))))



(defn assoc-plate-ids-with-plate-set-id
  "plate-ids: integer array of plate ids  int[]
  plate-set-id integer"
  [ plate-ids plate-set-id ]
  (let [
        sorted-plate-ids (sort plate-ids)
        plate-order (range 1 (+ 1 (count sorted-plate-ids)))
        content (pairs sorted-plate-ids plate-order)
        sql-statement (str "INSERT INTO plate_plate_set (plate_set_id, plate_id, plate_order) VALUES (" (str plate-set-id)", ?,?)")
        ]
      (with-open [con (j/get-connection dbm/pg-db)
                  ps  (j/prepare con [sql-statement])]
        (p/execute-batch! ps content))    
      ))


(defn new-user
  ;;tags are any keyword
  ;; group-id is int
  [ user-name tags password group-id ]
  (let [ sql-statement (str "INSERT INTO lnuser(usergroup, lnuser_name, tags, password) VALUES (?, ?, ?, ?)")
        ]
  (j/execute-one! dbm/pg-db [sql-statement group-id user-name tags password])))

(defn new-project
  ;;tags are any keyword
  ;; group-id is int
  [ project-name description lnuser-id ]
  (let [ sql-statement (str "INSERT INTO project(descr, project_name, lnsession_id) VALUES (?, ?, ?)")
        new-project-id-pre (j/execute-one! dbm/pg-db [sql-statement description project-name lnuser-id]{:return-keys true})
        new-project-id (:project/id new-project-id-pre)
        ]
  (j/execute-one! dbm/pg-db [(str "UPDATE project SET project_sys_name = " (str "'PRJ-" new-project-id "'") " WHERE id=?") new-project-id])))


;;https://github.com/seancorfield/next-jdbc/blob/master/test/next/jdbc_test.clj#L53-L105

(defn get-ids-for-sys-names
  "sys_names vector of system_names
   table table to be queried
   column name of the sys_name column e.g. plate_sys_name, plate_set_sys_name
  execute-multi! not returning the result so this is a hack"
  [sys-names table column-name]
  (into [] (map :plate_set/id
       (flatten
        (let [ sql-statement (str "SELECT id FROM " table  "  WHERE " column-name  " = ?")
              ;;content (into [](map vec (partition 1  sys-names)))
              con (j/get-connection  dbm/pg-db)
              ;;ps  (j/prepare con [sql-statement ])
              results nil]
          (for [x sys-names]  (concat results (j/execute! con  [sql-statement x]))))))))


;;(get-ids-for-sys-names ["PS-1" "PS-2" "PS-3" "PS-4" ] "plate_set" "plate_set_sys_name" )
;;(get-ids-for-sys-names ["PS-10"] "plate_set" "plate_set_sys_name" )



(defn get-all-plate-ids-for-plate-set-id [ plate-set-id]
  (let [ sql-statement "SELECT plate_id  FROM  plate_plate_set WHERE plate_plate_set.plate_set_id = ?;"
         plate-ids-pre (doall (j/execute! dbm/pg-db [sql-statement plate-set-id]{:return-keys true}))
        ]
    (into [] (map :plate_plate_set/plate_id (flatten plate-ids-pre)))))


;;must get rid of import file in Utilities.java

(defn create-assay-run
  " String _assayName,
      String _descr,
      int _assay_type_id,
      int _plate_set_id,
      int _plate_layout_name_id
  "
  [ assay-run-name description assay-type-id plate-set-id plate-layout-name-id ]
  (let [ session-id (cm/get-session-id)
        sql-statement (str "INSERT INTO assay_run(assay_run_name , descr, assay_type_id, plate_set_id, plate_layout_name_id, lnsession_id) VALUES (?, ?, ?, ?, ?, " session-id ")")
        new-assay-run-id-pre (j/execute-one! dbm/pg-db [sql-statement assay-run-name description assay-type-id plate-set-id plate-layout-name-id ]{:return-keys true})
        new-assay-run-id (:assay_run/id new-assay-run-id-pre)
        ]
    (j/execute-one! dbm/pg-db [(str "UPDATE assay_run SET assay_run_sys_name = " (str "'AR-" new-assay-run-id "'") " WHERE id=?") new-assay-run-id])
    new-assay-run-id))

;; used to process and  load manipulated maps
;; defn process-assay-results-to-load
;; "order is important; must correlate with SQL statement order of ?'s"
;;   [x]
;;  (into [] [(Integer/parseInt(:plate x )) (Integer/parseInt(:well x)) (Double/parseDouble(:response x ))  (Double/parseDouble(:bkgrnd_sub x )) (Double/parseDouble(:norm x )) (Double/parseDouble(:norm_pos x )) (Double/parseDouble(:p_enhance x ))]))


;; (defn load-assay-results
;;   [ assay-run-id data-table]
;;   (let [ sql-statement (str "INSERT INTO assay_result( assay_run_id, plate_order, well, response ) VALUES ( " assay-run-id ", ?, ?, ?)")
;;         content (into [] (map #(process-assay-results-map %) data-table))
;;         ]
;;       (with-open [con (j/get-connection dbm/pg-db)
;;                   ps  (j/prepare con [sql-statement])]
;;         (p/execute-batch! ps content))))




(defn new-hit-list
"hit-list is a vector of integers"
  [ hit-list-name description number-of-hits assay-run-id hit-list]
  (let [
        lnsession-id (cm/get-session-id)
        sql-statement (str "INSERT INTO hit_list(hitlist_name, descr, n, assay_run_id, lnsession_id) VALUES ('" hit-list-name "', '" description "', " (str number-of-hits) ", " (str assay-run-id) ", " (str lnsession-id) ")")
        new-hit-list-id-pre (j/execute-one! dbm/pg-db [sql-statement]{:return-keys true})
        new-hit-list-id (:hit_list/id new-hit-list-id-pre)
        sql-statement2 (str "UPDATE hit_list SET hitlist_sys_name = 'HL-" (str new-hit-list-id) "' WHERE id=" (str new-hit-list-id))
        dummy (j/execute-one! dbm/pg-db [sql-statement2])
        sql-statement3 (str "INSERT INTO hit_sample(hitlist_id, sample_id) VALUES(" (str new-hit-list-id) ", ?)")
        content (into [](map vector hit-list))
        ]  
     (with-open [con (j/get-connection dbm/pg-db)
                 ps  (j/prepare con [sql-statement3])]
      (p/execute-batch! ps content))
     ))

(defn process-rearray-map-to-load
"order is important; must correlate with SQL statement order of ?'s"
  [x]
 (into [] [ (:id x ) (:source_plate_sys_name x) (:source_by_col x ) (:plate_sys_name x ) (:by_col x )]))


(defn rearray-transfer-samples 
  "used during rearray process
first selection: select get in plate, well order, not necessarily sample order "
  [ source-plate-set-id  dest-plate-set-id  hit-list-id]
  (let [ sql-statement1 "SELECT  sample.id FROM plate_set, plate_plate_set, plate, well, well_sample, sample WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id= ? AND sample.ID  IN  (SELECT hit_sample.sample_id FROM hit_sample WHERE hit_sample.hitlist_id = ?) ORDER BY plate.ID, well.ID"
        all-hit-sample-ids  (first (sorted-set (proto/-execute-all dbm/pg-db [ sql-statement1 source-plate-set-id hit-list-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} )))
        num-hits (count all-hit-sample-ids)
        sql-statement2 "SELECT well.ID FROM plate_set, plate_plate_set, plate, well, plate_layout WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND plate_set.plate_layout_name_id=plate_layout.plate_layout_name_id AND plate_layout.well_by_col= well.by_col AND plate_set.id= ? AND plate_layout.well_type_id=1 ORDER BY well.ID"
        dest-wells (take num-hits (first (sorted-set (proto/-execute-all dbm/pg-db [ sql-statement2 dest-plate-set-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))))
        hit-well (pairs  (map :id dest-wells) (map :id all-hit-sample-ids))
        sql-statement3 " INSERT INTO well_sample (well_id, sample_id) VALUES (?,?)"
        a      (with-open [con (j/get-connection dbm/pg-db)
                           ps  (j/prepare con [sql-statement3])]
                 (p/execute-batch! ps hit-well))
        sql-statement4 "INSERT INTO rearray_pairs (src, dest) VALUES (?,?)"
        rearray-pairs-id-pre (j/execute-one! dbm/pg-db [sql-statement4 source-plate-set-id dest-plate-set-id]{:return-keys true}) 
        rearray-pairs-id (:rearray_pairs/id rearray-pairs-id-pre)
        sql-statement5 "SELECT  plate.plate_sys_name AS \"source_plate_sys_name\", well.by_col AS \"source_by_col\", sample.ID   FROM plate_set, plate_plate_set, plate, well, well_sample, sample  WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id= ?  AND sample.ID IN  (SELECT hit_sample.sample_id FROM hit_sample WHERE hit_sample.hitlist_id = ? ORDER BY sample.ID)"
        orig-plates-with-hits (set (proto/-execute-all dbm/pg-db [ sql-statement5 source-plate-set-id hit-list-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))
        sql-statement6 "SELECT plate.plate_sys_name, well.by_col, sample.ID  FROM plate_set, plate_plate_set, plate, well, well_sample, sample  WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id= ?  ORDER BY sample.ID"
        new-plates-of-hits (set (proto/-execute-all dbm/pg-db [ sql-statement6 dest-plate-set-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))
        joined-data (s/join orig-plates-with-hits new-plates-of-hits{:id :id})     
        sql-statement7 (str "INSERT INTO worklists ( rearray_pairs_id, sample_id, source_plate, source_well, dest_plate, dest_well) VALUES (" (str rearray-pairs-id) ", ?, ?, ?, ?, ? )")
        content (into [] (map #(process-rearray-map-to-load %) joined-data))
        ]
      (with-open [con (j/get-connection dbm/pg-db)
                 ps  (j/prepare con [sql-statement7])]
      (p/execute-batch! ps content))))

(defn new-sample
  "not using this during plate creation.  Batching instead."
  [ project-id  ]
  (let [
        sql-statement "INSERT INTO sample(project_id) VALUES (?)"
        new-sample-id-pre (j/execute-one! dbm/pg-db [sql-statement project-id  ]{:return-keys true})
        new-sample-id (:sample/id new-sample-id-pre)
        sql-statement2 (str "UPDATE sample SET sample_sys_name = 'SPL-" (str new-sample-id)  "' WHERE id=?")
        a (j/execute-one! dbm/pg-db [sql-statement2 new-sample-id ]) 
        ]
    new-sample-id))

(defn new-plate
  "only add samples if include-samples is true"
  [plate-type-id plate-set-id plate-format-id plate-layout-name-id include-samples]
  (let [sql-statement1 "INSERT INTO plate(plate_type_id, plate_format_id, plate_layout_name_id) VALUES (?, ?, ?)"
        new-plate-id-pre (j/execute-one! dbm/pg-db [sql-statement1 plate-type-id plate-format-id plate-layout-name-id ]{:return-keys true})
        new-plate-id (:plate/id new-plate-id-pre)
        sql-statement2 (str "UPDATE plate SET plate_sys_name = 'PLT- " (str new-plate-id) "' WHERE id=?")
        a (j/execute-one! dbm/pg-db [sql-statement2 new-plate-id ])
        sql-statement3 (str "INSERT INTO well(by_col, plate_id) VALUES(?, " (str new-plate-id) ")")
        content (into [] (map vector (range 1 (+ 1 plate-format-id))))
        b  (with-open [con (j/get-connection dbm/pg-db)
                       ps  (j/prepare con [sql-statement3])]
             (p/execute-batch! ps content))      
        ]
    (if (= include-samples true)
      (let [  sql-statement4  (str "SELECT well.id  FROM plate_layout, plate_layout_name, plate, well  WHERE plate_layout.plate_layout_name_id = plate_layout_name.id AND plate_layout.well_type_id = 1 AND well.plate_id=plate.id AND plate_layout.plate_layout_name_id = ? AND plate_layout.well_by_col=well.by_col AND plate.id= ?")
            wells-need-samples (into [] (map vector (map :id (first (sorted-set (proto/-execute-all dbm/pg-db [ sql-statement4 plate-layout-name-id new-plate-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))))))
            project-id (cm/get-project-id)
            sql-statement5  "INSERT INTO sample( project_id, plate_id) VALUES(?, ?)"
            prj-plt (into []  (repeat (count wells-need-samples) [(cm/get-project-id) new-plate-id] ))
            c  (with-open [con (j/get-connection dbm/pg-db)
                           ps  (j/prepare con [sql-statement5])]
                 (p/execute-batch! ps prj-plt))
            sql-statement6 "SELECT id FROM  sample WHERE  plate_id=?"
            new-sample-ids-pre (set (proto/-execute-all dbm/pg-db [ sql-statement6 new-plate-id ]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))
            new-sample-ids  (map :id new-sample-ids-pre)
            sql-statement7 "UPDATE sample SET sample_sys_name = CONCAT('SPL-', ?) WHERE id=?"
            content (into [] (pairs  (sort  new-sample-ids)  (sort  new-sample-ids)))
            d  (with-open [con (j/get-connection dbm/pg-db)
                           ps  (j/prepare con [sql-statement7])]
                 (p/execute-batch! ps content)) 
            sql-statement8 "INSERT INTO well_sample(well_id, sample_id)VALUES(?,?)"
            well-sample-pairs (into [] (pairs  (flatten wells-need-samples)  (sort  new-sample-ids)))
            ]
        (with-open [con (j/get-connection dbm/pg-db)
                    ps  (j/prepare con [sql-statement8])]
          (p/execute-batch! ps well-sample-pairs)) 

              ))new-plate-id))

;;(new-plate 1 50 96 1 true)



(defn new-plate-set [ description, plate-set-name, num-plates, plate-format-id, plate-type-id, project-id, plate-layout-name-id, with-samples ]
  (let [
        lnsession-id (cm/get-session-id)
        sql-statement "INSERT INTO plate_set(descr, plate_set_name, num_plates, plate_format_id, plate_type_id, project_id, plate_layout_name_id, lnsession_id) VALUES (?, ?, ?, ?, ?, ?, ?, ? )"
        new-plate-set-id-pre (j/execute-one! dbm/pg-db [sql-statement description plate-set-name num-plates plate-format-id plate-type-id project-id plate-layout-name-id lnsession-id ]{:return-keys true})
        new-plate-set-id (:plate_set/id new-plate-set-id-pre)
        sql-statement2 (str "UPDATE plate_set SET plate_set_sys_name = 'PS-" (str new-plate-set-id)  "'WHERE id=?")
        a (j/execute-one! dbm/pg-db [sql-statement2 new-plate-set-id ]) 
        ]
        (loop [
               new-plate-ids #{}
               plate-counter 1]
          (if (< plate-counter (+ 1 num-plates))
            (recur (s/union  new-plate-ids #{ (new-plate plate-type-id new-plate-set-id plate-format-id plate-layout-name-id with-samples)}) (inc plate-counter))
            (let [ ;;once set is full
                  sql-statement5 "UPDATE plate SET plate_sys_name = CONCAT('PLT-', ?) WHERE id=?"
                   content (into [] (pairs (flatten (sort (map vector new-plate-ids)))(flatten (sort (map vector new-plate-ids)))))
                  a  (with-open [con (j/get-connection dbm/pg-db)
                                ps  (j/prepare con [sql-statement5])]
                       (p/execute-batch! ps content )) 
                  sql-statement6 (str "INSERT INTO plate_plate_set(plate_set_id, plate_id, plate_order) VALUES(" (str new-plate-set-id) ",?,?)")
                  plate-id-order (into [] (pairs  (flatten (sort (map vector new-plate-ids))) (range 1 (+ 1 num-plates))))
                  ]
               (with-open [con (j/get-connection dbm/pg-db)
                        ps  (j/prepare con [sql-statement6])]
                  (p/execute-batch! ps plate-id-order)))))   ;;remove 3
  new-plate-set-id))

;;(new-plate-set "des" "ps name" 3 96 1 1 1 1 false)
 (defn process-swell-dwell-to-load
 "order is important; must correlate with SQL statement order of ?'s"
   [x]
  (into [] [ (:id x) (:source_well_id x) ]))



(defn reformat-plate-set
  "Called from DialogReformatPlateSet OK action listener"
  [source-plate-set-id  source-num-plates  n-reps-source  dest-descr  dest-plate-set-name  dest-num-plates  dest-plate-format-id  dest-plate-type-id  dest-plate-layout-name-id ]
  (let [
        project-id (cm/get-project-id)
        dest-plate-set-id (new-plate-set dest-descr, dest-plate-set-name, dest-num-plates, dest-plate-format-id, dest-plate-type-id, project-id, dest-plate-layout-name-id, false )
        sql-statement1 "select well.plate_id, plate_plate_set.plate_order, well.by_col, well.id AS source_well_id FROM plate_plate_set, well  WHERE plate_plate_set.plate_set_id = ? AND plate_plate_set.plate_id = well.plate_id ORDER BY well.plate_id, well.ID"
        source-plates (proto/-execute-all dbm/pg-db [ sql-statement1 source-plate-set-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} )
        rep-source-plates (loop [  counter 1 temp ()]
                            (if (> counter n-reps-source)  temp
                                (recur   (+ 1 counter)
                                         (concat (map #(assoc % :rep counter) source-plates) temp))))

       
        sorted-source-pre    (sort-by (juxt :plate_id :rep :source_well_id)  rep-source-plates)
        num  (count sorted-source-pre)
         sorted-source (into [] (loop [  counter 0
                                       new-set #{}
                                       remaining sorted-source-pre]
                                  (if (> counter  (- num 1 ))  new-set
                                      (recur   (+ 1 counter)
                                               (s/union new-set #{(assoc (first remaining) :sort-order counter)})
                                               (rest remaining)))))
        sql-statement2 "SELECT plate_plate_set.plate_ID, well.by_col,  well.id, well_numbers.well_name, well_numbers.quad  FROM well, plate_plate_set, well_numbers, plate_layout  WHERE plate_plate_set.plate_set_id = ?  AND plate_plate_set.plate_id = well.plate_id AND well_numbers.plate_format= ? AND well.by_col = well_numbers.by_col AND plate_layout.plate_layout_name_id = ? AND well.by_col=plate_layout.well_by_col AND plate_layout.well_type_id = 1 order by plate_id, quad, well_numbers.by_col"
        dest-plates-unk-wells (proto/-execute-all dbm/pg-db [ sql-statement2 dest-plate-set-id dest-plate-format-id dest-plate-layout-name-id]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} )
        sorted-dest-pre    (sort-by (juxt :plate_id :quad :by_col)  dest-plates-unk-wells)
        num-dest  (count sorted-dest-pre)
        sorted-dest (into [] (loop [  counter 0
                                    new-set #{}
                                    remaining sorted-dest-pre]
                               (if (> counter  (- num-dest 1 ))  new-set
                                   (recur   (+ 1 counter)
                                            (s/union new-set #{(assoc (first remaining) :sort-order counter)})
                                            (rest remaining)))))
        joined-data  (s/join sorted-source sorted-dest {:sort-order :sort-order})
        dwell-swell (map #(process-swell-dwell-to-load %) joined-data)
        sql-statement3 "INSERT INTO well_sample (well_id, sample_id) VALUES ( ?, (SELECT sample.id FROM sample, well, well_sample WHERE well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.id= ?))" 
        ]
    (with-open [con (j/get-connection dbm/pg-db)
                ps  (j/prepare con [sql-statement3])]
      (p/execute-batch! ps dwell-swell))
    dest-plate-set-id))

;;(reformat-plate-set 3 2 1 "desr1" "reformatted PS3" 1 384 1 1 19)


(defn process-assay-results-to-load
"order is important; must correlate with SQL statement order of ?'s"
  [x]
 (into [] [(:plate x ) (:well x) (:response x ) (:bkgrnd_sub x ) (:norm x ) (:norm_pos x ) (:p_enhance x )]))



(defn process-plate-of-data [plate-data id]
        (let [ 
                ;;plate-data (s/select #(= (:plate %) individual-plate) joined-data)
                positives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 2) plate-data))))
                negatives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 3) plate-data))))
                background (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 4) plate-data))))
                unk-max (last(sort (map #(get % :response)(into [](s/select #(= (:well_type_id %) 1) plate-data)))))
              ]
           (loop [
                 processed-results-set #{}
                 well-number 1]
            (if (> well-number  (count plate-data));;once all wells processed
              processed-results-set
              (let [
                    response (:response (first (into [] (s/select #(= (:well %) well-number) plate-data))))
                    bkgrnd_sub (- response background)
                    norm  (/ response unk-max)
                    norm_pos (/ response positives)
                    p_enhance (* 100(- (/ (- response negatives) (- positives negatives)) 1))
                    ]
                   (recur 
                   (s/union  processed-results-set #{{:plate id :well well-number :response response :bkgrnd_sub bkgrnd_sub :norm norm :norm_pos norm_pos :p_enhance p_enhance}})
                   (+ 1 well-number)))))))




(defn process-assay-results-map
;;used when manipulating maps before loading postgres
  [x]
(into {} { :plate (Integer/parseInt(:plate x )) :well (Integer/parseInt(:well x)) :response (Double/parseDouble(:response x ))}))

  
(defn associate-data-with-plate-set
  "  String _plate_set_sys_name,  a vector of sys-name; 
      int _top_n_number"
  [assay-run-name description plate-set-sys-names format-id assay-type-id plate-layout-name-id input-file-name auto-select-hits hit-selection-algorithm top-n-number]
 (let [ plate-set-ids (get-ids-for-sys-names plate-set-sys-names "plate_set" "plate_set_sys_name");;should only be one though method handles array 
       num-of-plate-ids (count (get-all-plate-ids-for-plate-set-id (first plate-set-ids))) ;;should be only one plate-set-id
       expected-rows-in-table  (* num-of-plate-ids format-id)
       table-map (table-to-map input-file-name)
       ]
   (if (= expected-rows-in-table (count table-map))
     ;;^^ first let determines if file is valid
     ;;plate-set-ids could be a vector with many but for this workflow only expecting one; get it out with (first)
     ;;vv second let does processing
     (let [
           sql-statement "SELECT well_by_col, well_type_id, replicates, target FROM plate_layout WHERE plate_layout_name_id =?"
           layout  (set (proto/-execute-all dbm/pg-db [ sql-statement 1]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))
           data (set (map #(process-assay-results-map %) (table-to-map input-file-name)))
           joined-data (s/join data layout {:well :well_by_col})
           num-plates   (count (distinct (map :plate  joined-data)))
           processed-plates  (loop [plate-counter 1
                                    plate-of-data  (s/select #(= (:plate %) plate-counter) joined-data)                                
                                    new-set #{}]
                               (if (> plate-counter num-plates) new-set
                                   (recur (+ 1 plate-counter)
                                          (s/select #(= (:plate %) plate-counter) joined-data)
                                          (s/union new-set (process-plate-of-data plate-of-data plate-counter)))))
           a (s/project processed-plates [:plate :well :response :bkgrnd_sub :norm :norm_pos :p_enhance])
           b (into [] a)
           content (into [] (map #(process-assay-results-to-load %) b))
           new-assay-run-id (create-assay-run  assay-run-name description assay-type-id (first plate-set-ids) plate-layout-name-id )
           sql-statement (str "INSERT INTO assay_result( assay_run_id, plate_order, well, response, bkgrnd_sub, norm, norm_pos, p_enhance ) VALUES ( "  new-assay-run-id ", ?, ?, ?, ?, ?, ?, ?)")
           ]                                  
           (with-open [con (j/get-connection dbm/pg-db)
                              ps  (j/prepare con [sql-statement])]
             (p/execute-batch! ps content))
;;          (println joined-data)
           new-assay-run-id  ;;must return new id for auto-select-hits when true
       ;;(clojure.pprint/pprint processed-plates)
       )      ;;end of second let
 (javax.swing.JOptionPane/showMessageDialog nil (str "Expecting " expected-rows-in-table " rows but found " (count table-map) " rows in data file." )) )));;row count is not correct

;;(associate-data-with-plate-set "run1test" "test-desc" ["PS-2"] 96 1 1 "/home/mbc/sample96controls4lowp1.txt" true 1 10)


(defn process-source-layout
  "replication and target are 1"
  [x]
(let [ a (rest x)
      num (count a) ]
  (loop [ new-set #{}
         first-item (first a)
         remaining (rest a)
         counter 1]
    (if(= counter num) new-set   
       (recur     
        (s/union new-set #{[(Integer/parseInt (first first-item)) (Integer/parseInt (first(rest first-item))) 1 1]})
        (first remaining)
        (rest remaining)
        (+ 1 counter)
        )))) )


(defn new-plate-layout
"data is an array"
  [ data  source-name  source-description control-loc n-controls n-unk source-format-id  n-edge  ]
  (let [dest-layout-descr [["1S4T"]["2S2T"]["2S4T"]["4S1T"]["4S2T"]]
        edge (if (> 0 n-edge) 0 1)
        dest-layout-ids (if (= 96 source-format-id) [2 3 4 5 6] [14 15 16 17 18]) ;;if not 96 then 384 only options
        dest-format (if (= 96 source-format-id) 384 1536)
        sql-statement1 (str "INSERT INTO plate_layout_name(name, descr, plate_format_id, replicates, targets, use_edge, num_controls, unknown_n, control_loc, source_dest) VALUES (?,?,?,?,?,?,?,?, 'source')")
        source-plate-layout-name-id-pre (j/execute-one! dbm/pg-db [sql-statement1 source-name source-description source-format-id 1 1 edge n-controls n-unk control-loc ]{:return-keys true})
       source-plate-layout-name-id (:plate_layout_name/id source-plate-layout-name-id-pre)
        sql-statement2  "UPDATE plate_layout_name SET sys_name = CONCAT('LYT-', ?) WHERE id=?" 
        a (j/execute-one! dbm/pg-db [sql-statement2 new-plate-layout-name-id new-plate-layout-name-id])
        ;;insert the source layout
       
        sql-statement3 "INSERT INTO plate_layout( plate_layout_name_id, well_by_col, well_type_id, replicates, target ) VALUES (?,?,?,?,?)"
        source-data (process-source-layout data)
        b            (with-open [con (j/get-connection dbm/pg-db)
                              ps  (j/prepare con [sql-statement3])]
                       (p/execute-batch! ps source-data))
        sql-statement-name (str "INSERT INTO plate_layout_name ( descr, plate_format_id, replicates, targets, use_edge, num_controls, unknown_n, control_loc, source_dest) VALUES ( ?, ?, 1, 1, ?, ?, ?, ?, 'dest')")
        sql-statement-name-update "UPDATE plate_layout_name SET sys_name = CONCAT('LYT-',?) WHERE id=?")
        sql-statement-layout (str "INSERT INTO plate_layout (SELECT dest_id AS \"plate_layout_name_id\", well_numbers.by_col AS \"well_by_col\", import_plate_layout.well_type_id, plate_layout.replicates, plate_layout.target FROM well_numbers, import_plate_layout, plate_layout WHERE well_numbers.plate_format = ? AND import_plate_layout.well_by_col=well_numbers.parent_well AND plate_layout.plate_layout_name_id= ?  AND plate_layout.well_by_col=well_numbers.by_col)")
        sql-statement-src-dest "INSERT INTO layout_source_dest (src, dest) VALUES (source_id, dest_id)"
        ]
  (loop [
         dl-descr-first (first dest-layout-descr)
         dl-desc-rest (rest dest-layout-descr)
         dest-id (j/execute-one! dbm/pg-db [sql-statement-name dl-descr-first dest-format n-controls n-unk control-loc ]{:return-keys true})
         dummy  (j/execute-one! dbm/pg-db [sql-statement-name-update dest-id dest-id])
         dummy2 (j/execute-one! dbm/pg-db [sql-statement-layout dest-format dest-id ])
         dummy3 (j/execute-one! dbm/pg-db [sql-statement-src-dest source-plate-layout-name-id dest-id])
         counter 1
         ]
    (if (= counter 5))
    (recur
     (first dl-desc-rest)
     (rest dl-desc-rest)
     (j/execute-one! dbm/pg-db [sql-statement-name dl-descr-first dest-format n-controls n-unk control-loc ]{:return-keys true})
     (j/execute-one! dbm/pg-db [sql-statement-name-update dest-id dest-id])
     (j/execute-one! dbm/pg-db [sql-statement-layout dest-format dest-id ])
     (j/execute-one! dbm/pg-db [sql-statement-src-dest source-plate-layout-name-id dest-id])
     (+ 1 counter)
    )
  ))

;;(new-plate-layout a "MyLayoutName" "1S1T" "scattered" 8 300 384 76 )


(def a [["well", "type"], ["1", "5"], ["2", "5"], ["3", "5"], ["4", "5"],
 ["5", "5"], ["6", "5"], ["7", "5"], ["8", "5"], ["9", "5"],
 ["10", "5"], ["11", "5"], ["12", "5"], ["13", "5"], ["14", "5"],
 ["15", "5"], ["16", "5"], ["17", "5"], ["18", "1"], ["19", "1"],
 ["20", "1"], ["21", "1"], ["22", "1"], ["23", "1"], ["24", "1"],
 ["25", "1"], ["26", "1"], ["27", "1"], ["28", "1"], ["29", "1"],
 ["30", "1"], ["31", "1"], ["32", "5"], ["33", "5"], ["34", "1"],
 ["35", "1"], ["36", "1"], ["37", "1"], ["38", "1"], ["39", "1"],
 ["40", "1"], ["41", "1"], ["42", "1"], ["43", "1"], ["44", "1"],
 ["45", "1"], ["46", "1"], ["47", "1"], ["48", "5"], ["49", "5"],
 ["50", "1"], ["51", "1"], ["52", "1"], ["53", "1"], ["54", "1"],
 ["55", "2"], ["56", "1"], ["57", "1"], ["58", "1"], ["59", "1"],
 ["60", "1"], ["61", "1"], ["62", "1"], ["63", "1"], ["64", "5"],
 ["65", "5"], ["66", "1"], ["67", "1"], ["68", "1"], ["69", "1"],
 ["70", "1"], ["71", "1"], ["72", "1"], ["73", "1"], ["74", "1"],
 ["75", "1"], ["76", "1"], ["77", "1"], ["78", "1"], ["79", "1"],
 ["80", "5"], ["81", "5"], ["82", "1"], ["83", "1"], ["84", "1"],
 ["85", "1"], ["86", "1"], ["87", "1"], ["88", "1"], ["89", "1"],
 ["90", "1"], ["91", "1"], ["92", "1"], ["93", "1"], ["94", "1"],
 ["95", "1"], ["96", "5"], ["97", "5"], ["98", "3"], ["99", "1"],
 ["100", "1"], ["101", "1"], ["102", "1"], ["103", "1"], ["104", "1"],
 ["105", "1"], ["106", "1"], ["107", "1"], ["108", "1"], ["109", "1"],
 ["110", "1"], ["111", "1"], ["112", "5"], ["113", "5"], ["114", "1"],
 ["115", "1"], ["116", "1"], ["117", "1"], ["118", "1"], ["119", "1"],
 ["120", "1"], ["121", "1"], ["122", "1"], ["123", "1"], ["124", "1"],
 ["125", "2"], ["126", "1"], ["127", "1"], ["128", "5"], ["129", "5"],
 ["130", "1"], ["131", "1"], ["132", "1"], ["133", "1"], ["134", "1"],
 ["135", "1"], ["136", "1"], ["137", "1"], ["138", "1"], ["139", "1"],
 ["140", "1"], ["141", "1"], ["142", "1"], ["143", "1"], ["144", "5"],
 ["145", "5"], ["146", "1"], ["147", "1"], ["148", "1"], ["149", "1"],
 ["150", "1"], ["151", "1"], ["152", "1"], ["153", "1"], ["154", "1"],
 ["155", "1"], ["156", "1"], ["157", "1"], ["158", "1"], ["159", "1"],
 ["160", "5"], ["161", "5"], ["162", "1"], ["163", "1"], ["164", "1"],
 ["165", "1"], ["166", "1"], ["167", "1"], ["168", "1"], ["169", "1"],
 ["170", "1"], ["171", "1"], ["172", "1"], ["173", "1"], ["174", "1"],
 ["175", "1"], ["176", "5"], ["177", "5"], ["178", "1"], ["179", "1"],
 ["180", "1"], ["181", "1"], ["182", "1"], ["183", "1"], ["184", "4"],
 ["185", "1"], ["186", "1"], ["187", "1"], ["188", "1"], ["189", "1"],
 ["190", "1"], ["191", "1"], ["192", "5"], ["193", "5"], ["194", "1"],
 ["195", "1"], ["196", "1"], ["197", "1"], ["198", "1"], ["199", "1"],
 ["200", "1"], ["201", "1"], ["202", "1"], ["203", "1"], ["204", "1"],
 ["205", "1"], ["206", "1"], ["207", "1"], ["208", "5"], ["209", "5"],
 ["210", "1"], ["211", "1"], ["212", "1"], ["213", "1"], ["214", "1"],
 ["215", "1"], ["216", "1"], ["217", "1"], ["218", "1"], ["219", "1"],
 ["220", "1"], ["221", "1"], ["222", "1"], ["223", "1"], ["224", "5"],
 ["225", "5"], ["226", "1"], ["227", "1"], ["228", "1"], ["229", "1"],
 ["230", "1"], ["231", "1"], ["232", "1"], ["233", "1"], ["234", "1"],
 ["235", "1"], ["236", "1"], ["237", "1"], ["238", "1"], ["239", "1"],
 ["240", "5"], ["241", "5"], ["242", "1"], ["243", "1"], ["244", "1"],
 ["245", "1"], ["246", "1"], ["247", "1"], ["248", "1"], ["249", "1"],
 ["250", "1"], ["251", "1"], ["252", "1"], ["253", "1"], ["254", "1"],
 ["255", "1"], ["256", "5"], ["257", "5"], ["258", "1"], ["259", "1"],
 ["260", "1"], ["261", "1"], ["262", "1"], ["263", "1"], ["264", "1"],
 ["265", "1"], ["266", "1"], ["267", "1"], ["268", "1"], ["269", "1"],
 ["270", "2"], ["271", "1"], ["272", "5"], ["273", "5"], ["274", "1"],
 ["275", "1"], ["276", "1"], ["277", "1"], ["278", "1"], ["279", "1"],
 ["280", "1"], ["281", "1"], ["282", "1"], ["283", "1"], ["284", "1"],
 ["285", "1"], ["286", "1"], ["287", "1"], ["288", "5"], ["289", "5"],
 ["290", "1"], ["291", "1"], ["292", "1"], ["293", "1"], ["294", "1"],
 ["295", "1"], ["296", "1"], ["297", "1"], ["298", "1"], ["299", "1"],
 ["300", "1"], ["301", "1"], ["302", "1"], ["303", "1"], ["304", "5"],
 ["305", "5"], ["306", "1"], ["307", "1"], ["308", "1"], ["309", "1"],
 ["310", "1"], ["311", "1"], ["312", "1"], ["313", "1"], ["314", "1"],
 ["315", "1"], ["316", "1"], ["317", "1"], ["318", "1"], ["319", "1"],
 ["320", "5"], ["321", "5"], ["322", "1"], ["323", "1"], ["324", "2"],
 ["325", "1"], ["326", "1"], ["327", "1"], ["328", "1"], ["329", "1"],
 ["330", "1"], ["331", "1"], ["332", "1"], ["333", "1"], ["334", "1"],
 ["335", "1"], ["336", "5"], ["337", "5"], ["338", "1"], ["339", "1"],
 ["340", "1"], ["341", "1"], ["342", "1"], ["343", "1"], ["344", "1"],
 ["345", "1"], ["346", "4"], ["347", "1"], ["348", "1"], ["349", "1"],
 ["350", "1"], ["351", "1"], ["352", "5"], ["353", "5"], ["354", "1"],
 ["355", "1"], ["356", "3"], ["357", "1"], ["358", "1"], ["359", "1"],
 ["360", "1"], ["361", "1"], ["362", "1"], ["363", "1"], ["364", "1"],
 ["365", "1"], ["366", "1"], ["367", "1"], ["368", "5"], ["369", "5"],
 ["370", "5"], ["371", "5"], ["372", "5"], ["373", "5"], ["374", "5"],
 ["375", "5"], ["376", "5"], ["377", "5"], ["378", "5"], ["379", "5"],
        ["380", "5"], ["381", "5"], ["382", "5"], ["383", "5"], ["384", "5"]])


(def b  [["well", "type"], ["1", "5"], ["2", "5"], ["3", "5"], ["4", "5"],
         ["5", "5"]])

(first (first (rest b)))
(def c (rest b))
(first c)
(let [ c (rest b)
      num (count c)    
      ]
  (println num)
  (loop [ new-set #{}
         first-item (first c)
         remaining (rest c)
         counter 1]
    (if(= counter num) new-set
       
       ( recur
       
       (s/union new-set #{[(Integer/parseInt (first first-item)) (Integer/parseInt (first(rest first-item))) 1 1]})
        (first remaining)
        (rest remaining)
        (+ 1 counter)
        ))

    ))

(Integer/parseInt (first (first c)))(Integer/parseInt (first(rest (first c))))
