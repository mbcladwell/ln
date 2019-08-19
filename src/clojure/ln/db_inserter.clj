(ns ln.db-inserter
  (:require [next.jdbc :as j]
            [next.jdbc.prepare :as p]
            [honeysql.core :as hsql]
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


;;!!!Not yet implemented!!!
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
