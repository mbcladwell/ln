(ns ln.db-retriever
  (:require [next.jdbc :as j]
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
    (count (rest result))))



(get-num-samples-for-plate-set 1)


(def get-num-samples-for-plate-setfunction ["CREATE OR REPLACE FUNCTION get_num_samples_for_plate_set(_plate_set_id INTEGER)
  RETURNS INTEGER AS
$BODY$
DECLARE
   psid int := _plate_set_id;
   
   counter INTEGER;
   sql_statement VARCHAR;
all_sample_ids INTEGER[];
num_samples INTEGER;
   
BEGIN

sql_statement := 'SELECT ARRAY(SELECT sample.id FROM plate, plate_plate_set, well, sample, well_sample WHERE plate_plate_set.plate_set_id = ' || psid || ' AND plate_plate_set.plate_id = plate.id AND well.plate_id = plate.id AND well_sample.well_id = well.id AND well_sample.sample_id = sample.id ORDER BY plate_plate_set.plate_id, plate_plate_set.plate_order, well.id)';

--    RAISE notice 'sql_statement: (%)', sql_statement;

     EXECUTE sql_statement INTO all_sample_ids;
     num_samples := array_length(all_sample_ids ,1); 
 -- RAISE notice 'ids: (%)', all_sample_ids;
 -- RAISE notice 'num: (%)', num_samples;

RETURN num_samples;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])
