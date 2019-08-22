(ns ln.db-inserter
  (:require [next.jdbc :as j]
            [next.jdbc.prepare :as prepare]
                   [next.jdbc.result-set :as rs]
       [next.jdbc.protocols :as proto]
       [clojure.set :as s]
       [honeysql.core :as hsql]
       [incanter.stats :as is]
            [honeysql.helpers :refer :all :as helpers]
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

;; used when direct load of assay file with stored procedure processing
;;(defn process-assay-results-map
;;order is important; must correlate with SQL statement order of ?'s
;;  [x]
;;(into [] [(Integer/parseInt(:plate x )) (Integer/parseInt(:well x)) (Double/parseDouble(:response x ))]))


(defn process-assay-results-map
;;used when manipulating maps before loading postgres
  [x]
(into {} { :plate (Integer/parseInt(:plate x )) :well (Integer/parseInt(:well x)) :response (Double/parseDouble(:response x ))}))

(defn load-assay-results
  [ assay-run-id data-table]
  (let [ sql-statement (str "INSERT INTO assay_result( assay_run_id, plate_order, well, response ) VALUES ( " assay-run-id ", ?, ?, ?)")
        content (into [] (map #(process-assay-results-map %) data-table))
        ]
      (with-open [con (j/get-connection dbm/pg-db)
                  ps  (j/prepare con [sql-statement])]
        (p/execute-batch! ps content))))


(defn associate-data-with-plate-set
  "      String _assayName,
      String _descr,
      String _plate_set_sys_name,  a vector of sys-name; 
      int _format_id,
      int _assay_type_id,
      int _plate_layout_name_id,
      ArrayList<String[]> _table,
      boolean _auto_select_hits,
      int _hit_selection_algorithm,
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
     (let [new-assay-run-id (create-assay-run  assay-run-name description assay-type-id (first plate-set-ids) plate-layout-name-id )
           sql-statement "SELECT well_by_col, well_type_id, replicates, target from plate_layout where plate_layout_name_id =?"
           layout  (set (proto/-execute-all dbm/pg-db [ sql-statement 1]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} ))
           data (set (map #(process-assay-results-map %) (table-to-map "/home/mbc/sample96controls4lowp1.txt")))
           joined-data (s/join data layout {:well :well_by_col})
           plate-list (distinct (map :plate  joined-data))
           
           ]
       (load-assay-results new-assay-run-id table-map)
       new-assay-run-id)
     ;;end of second let
 (javax.swing.JOptionPane/showMessageDialog nil (str "Expecting " expected-rows-in-table " rows but found " (count table-map) " rows in data file.") ))));;row count is not correct

;;(associate-data-with-plate-set "mynewassay" "descr1" ["PS-2"] 96 1 1 "/home/mbc/sample96controls4lowp1.txt" true 1 10)  


(def sql-statement "SELECT well_by_col, well_type_id, replicates, target from plate_layout where plate_layout_name_id =?")

(def layout  (set (proto/-execute-all dbm/pg-db [ sql-statement 1]{:label-fn rs/as-unqualified-maps :builder-fn rs/as-unqualified-maps} )))

(def data (set (map #(process-assay-results-map %) (table-to-map "/home/mbc/sample96controls4lowp1.txt"))))
(def joined-data (s/join data layout {:well :well_by_col}))

(def plate-list (distinct (map :plate  joined-data)))

(defn process-joined-data [ plate-order ]
  (let [
        plate-data (s/select #(= (:plate %) plate-order) joined-data)
        positives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 2) plate-data))))
        negatives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 3) plate-data))))
        background (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 4) plate-data))))
        unk-max (last(sort (map #(get % :response)(into [](s/select #(= (:well_type_id %) 1) plate-data)))))
        processed-results-set #{}
        ]

    )
  )

(defn process-well-data [ well-number]
  (let [
        response (:response (first (into [] (s/select #(= (:well %) well-number) plate-data))))
        bkgrnd_sub (- response background)
        norm  (/ response unk_max)
        norm_pos (/ response positives)
        p_enhance (* 100(- (/ (- response negatives) (- positives negatives)) 1))
        ]
    (s/union  processed-results-set #{{:well well-number :response response :bkgrnd_sub }}))
  )

  (de)
(def plate-data (s/select #(= (:plate %) 1) joined-data))
;;(def plate-data (into [](s/select #(= (:plate %) 1) joined-data)))
;;(map #( (:well_type_id %) 2) plate-data)

(def positives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 2) plate-data)))))
(def negatives (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 3) plate-data)))))
(def background (is/mean (map #(get % :response)(into [](s/select #(= (:well_type_id %) 4) plate-data)))))
(def unk-max (last(sort (map #(get % :response)(into [](s/select #(= (:well_type_id %) 1) plate-data))))))

(def processed-results-set #{})
;;map over wells which is format-id

  (def well-number 20)
  
(s/select #(= (:well %) well-number) plate-data)
(s/select  #(= (:well well-number) plate-data)


  
(:response (first (into [] (s/select #((:well 20) plate-data))))
  
(def response (:response (first (into [] (s/select #(= (:well %) 20) plate-data)))))
(def bkgrnd_sub (- response background))
(def norm  (/ response unk_max)))
(def norm_pos (/ response positives))
(def p_enhance (* 100(- (/ (- response negatives) (- positives negatives)) 1)))



(println plate-data)

(def myset #{{:well 1 :response 298776}})
(def myset (s/union  myset #{{:well 2 :response 475646}}))

(println myset)
