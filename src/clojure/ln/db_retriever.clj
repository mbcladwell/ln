(ns ln.db-retriever
  (:require [next.jdbc :as j]
              [next.jdbc.prepare :as p]
          [next.jdbc.result-set :as rs]
            [honeysql.core :as hsql]
            [honeysql.helpers :refer :all :as helpers]
           ;; [ln.db-manager :as dbm]
            [ln.codax-manager :as cm]
            [ln.db-manager :as dbm])
         ;;   [clojure.data.csv :as csv]
          ;;  [clojure.java.io :as io])
            
  (:import java.sql.DriverManager))




(defn authenticate-user
  ;;variable from codax
  []
  (let [user (cm/get-user)
        password (cm/get-password)
        results (j/execute-one! dbm/pg-db-admin ["SELECT lnuser.id, lnuser.password, lnuser_groups.id, lnuser_groups.usergroup  FROM lnuser, lnuser_groups  WHERE lnuser_groups.id = lnuser.usergroup and lnuser_name = ?"  user ])]
        (println (str "user: " user))
        (println password)
        (println results)
    (if (= password (:lnuser/password results) )
      (do
        (println "before uid ugid ug auth")
        (println  ( :lnuser_groups/usergroup results))
        (cm/set-uid-ugid-ug-auth
           (:lnuser/id  results)          
           ( :lnuser_groups/id results)
           ( :lnuser_groups/usergroup results)
           true)
        (println "after uid ugid ug auth")
          
          (dbm/define-pg-db));;valid
    (cm/set-authenticated false);;invalid
    )))

;;(authenticate-user)
;;(cm/print-ap)
;;(println dbm/pg-db)
;;(j/insert! dbm/pg-db :lnsession {:lnuser_id  1 } )


(defn register-session
  ;;user id
  [ uid ]
  (let [db (if (= (cm/get-source) "test") dbm/pg-db-admin-test dbm/pg-db-admin)
        user-id (j/execute-one! db ["INSERT INTO lnsession(lnuser_id) values(?)" uid]{:return-keys true} )
        ug-id (j/execute! db ["SELECT usergroup FROM lnuser WHERE lnuser.id =?"  (:lnsession/id user-id) ] )
        ]
    (cm/set-session-id  (:lnsession/id user-id) )
    (cm/set-user-group (:lnuser/usergroup ug-id))))


(defn get-num-samples-for-plate-set [ plate-set-id ]
  (let [
        sql-statement  (str "SELECT sample.id FROM plate, plate_plate_set, well, sample, well_sample WHERE plate_plate_set.plate_set_id = ? AND plate_plate_set.plate_id = plate.id AND well.plate_id = plate.id AND well_sample.well_id = well.id AND well_sample.sample_id = sample.id ORDER BY plate_plate_set.plate_id, plate_plate_set.plate_order, well.id")
        result (doall (j/execute! dbm/pg-db [ sql-statement plate-set-id]{:return-keys true} ))
        ]
    (count  result)))


;;https://github.com/seancorfield/next-jdbc/blob/master/test/next/jdbc_test.clj#L53-L105

(defn get-ids-for-sys-names
  "sys_names array of system_names
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


(def get-ids-for-sys-names ["CREATE OR REPLACE FUNCTION get_ids_for_sys_names( _sys_names VARCHAR[], _table VARCHAR(30), _sys_name VARCHAR(30))
  RETURNS integer[] AS
$BODY$
DECLARE
   sn varchar(20);
   an_int integer;
   sys_ids INTEGER[];
   sql_statement VARCHAR;
   sql_statement2 VARCHAR;
   
   temp INTEGER;

BEGIN

 sql_statement := 'SELECT id FROM ' || _table || ' WHERE ' || _sys_name   || ' = ';

  FOREACH sn IN ARRAY _sys_names
     LOOP
     sql_statement2 := sql_statement || quote_literal(sn);
     EXECUTE sql_statement2 INTO temp;
     sys_ids := array_append(sys_ids, temp );
    END LOOP;

RETURN sys_ids;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE PARALLEL UNSAFE; "])
