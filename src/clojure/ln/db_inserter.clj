(ns ln.db-inserter
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
            [clojure.string :only [split split-lines trim]]
 
            [ln.codax-manager :as cm]
            [ln.db-manager :as dbm])

         ;;    [ln.db-manager :as dbm])
         ;;   [clojure.data.csv :as csv]
           ;; [clojure.java.io :as io])
           
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
      barcodess looks like:

      plate 	barcode.id
      1     	AMRVK5473H
      1      	KMNCX9294W
      1      	EHRXZ2102Z
      1      	COZHR7852Q
      1      	FJVNR6433Q"
    
  (let [ col1name (first (get-col-names barcode-file))
        col2name (first (rest (get-col-names barcode-file)))
        table (table-to-map barcode-file)
        sql-statement (str "UPDATE plate SET barcode = ? WHERE plate.ID IN ( SELECT plate.id FROM plate_set, plate_plate_set, plate  WHERE plate_plate_set.plate_set_id=? AND plate_plate_set.plate_id=plate.id AND plate_plate_set.plate_order=? )")
        content (into [](map #(into [] (vals %))  (table-to-map "/home/mbc/barcodes.txt") ))
        ]
    (if (and (= col1name "plate")(= col2name "barcode.id"))
      (j/db-do-prepared dbm/pg-db sql-statement content)
      (javax.swing.JOptionPane/showMessageDialog nil  (str "Expecting the headers \"plate\", and \"barcode.id\", but found\n" col1name  ", and " col2name  "."  )))
      ))


(into [](map #(into [] (vals %))  (table-to-map "/home/mbc/barcodes.txt") ))

( apply #(vals % ) (table-to-map "/home/mbc/barcodes.txt"))
(import-barcode-ids 7 "/home/mbc/barcodes.txt")
(println dbm/pg-db)

"PS-7
PLT-20 -29
    1   10  order

CREATE OR REPLACE FUNCTION process_access_ids(ps_id INTEGER, sql_statement VARCHAR )
 RETURNS SETOF temp_accs_id AS
$BODY$
DECLARE
  r temp_accs_id%rowtype;
BEGIN

TRUNCATE temp_accs_id RESTART IDENTITY CASCADE;

execute sql_statement;
   FOR r IN
      SELECT * FROM temp_accs_id
   loop

UPDATE plate SET barcode = 'bc0001' WHERE plate.ID IN ( SELECT plate.id FROM plate_set, plate_plate_set, plate  WHERE plate_plate_set.plate_set_id=7 AND plate_plate_set.plate_id=plate.id    AND plate_plate_set.plate_order=1 );


select plate.id, plate.barcode, plate.plate_sys_name from plate, plate_set, plate_plate_set where plate_plate_set.plate_id=plate.id and plate_plate_set.plate_set_id=plate_set.id and plate_set.id=7; 

"

