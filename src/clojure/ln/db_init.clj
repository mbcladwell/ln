(ns ln.db-init
  (:require [clojure.java.jdbc :as jdbc]
            ;;[honeysql.core :as hsql]
            ;;[honeysql.helpers :refer :all :as helpers]
            [clojure.data.csv :as csv]
            [ln.codax-manager :as cm]
            [codax.core :as c]
            [ln.db-inserter :as dbi]
            [clojure.java.io :as io])
           
  (:import java.sql.DriverManager)
  (:gen-class))


(load "/ln/db_functions")
(load "/ln/example_data")


(def all-table-names
  ;;for use in a map function that will delete all tables
  ;;single command looks like:  (jdbc/drop-table-ddl :lnuser {:conditional? true } )
  ["well_numbers" "worklists" "rearray_pairs" "temp_accs_id" "import_plate_layout" "plate_layout" "well_type" "hit_sample" "hit_list" "assay_result" "assay_run" "assay_type" "well_sample" "sample" "well" "plate" "plate_plate_set" "plate_set" "layout_source_dest" "plate_layout_name" "plate_format" "plate_type" "project" "lnsession" "lnuser" "lnuser_groups"] )



(def all-tables
  ;;for use in a map function that will create all tables
  ;; example single table:
  ;;;     [(jdbc/create-table-ddl :lnsession
  ;;                 [[:id "SERIAL PRIMARY KEY"]
  ;;                  [:lnuser_id :int]
  ;;                  [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
 ;;                   ["FOREIGN KEY (lnuser_id) REFERENCES lnuser(id)"]]) ]

  [ 
   [(jdbc/create-table-ddl :lnuser_groups
                         [[:id "SERIAL PRIMARY KEY"]
                          [:usergroup "varchar(250)"]
                          [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]])]
   
   [(jdbc/create-table-ddl :lnuser
                          [[:id "SERIAL PRIMARY KEY"]
                           [:usergroup_id :int]
                           [:lnuser_name "VARCHAR(250) not null unique"]
                           [:tags "varchar(250)"]
                           [:password "varchar(64) not null"]
                           [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
                           ["FOREIGN KEY (usergroup_id) REFERENCES lnuser_groups(id)"]])]

 
   [(jdbc/create-table-ddl :lnsession
                         [[:id "SERIAL PRIMARY KEY"]
                          [:lnuser_id :int]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
                          ["FOREIGN KEY (lnuser_id) REFERENCES lnuser(id)"]]) ]
   
   [(jdbc/create-table-ddl :project
                           [[:id "SERIAL PRIMARY KEY"]
                            [:project_sys_name "varchar(30)"]
                            [:descr "varchar(250)"]
                            [:project_name "varchar(250)"]
                           [:lnsession_id :int]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
                            ["FOREIGN KEY (lnsession_id) REFERENCES lnsession(id)"]]
                           )]
   ;;CREATE INDEX ON project(lnsession_id);
   [(jdbc/create-table-ddl :plate_type
                           [[:id "SERIAL PRIMARY KEY"]
                            [:plate_type_name "varchar(30)"] ])]
   
   [(jdbc/create-table-ddl :plate_format
                           [[:id "SERIAL PRIMARY KEY"]
                            [:format "varchar(6)"]
                            [:rownum :int]
                            [:colnum :int]])]
   [(jdbc/create-table-ddl :plate_layout_name
                           [[:id "SERIAL PRIMARY KEY"]
                            [:sys_name "varchar(30)"]
                            [:name "varchar(250)"]
                            [:descr "varchar(250)"]
                            [:plate_format_id :int]
                            [:replicates :int]
                            [:targets :int]
                            [:use_edge :int]
                            [:num_controls :int]
                            [:unknown_n :int]
                            [:control_loc "varchar(30)"]
                            [:source_dest "varchar(30)"]
                            ["FOREIGN KEY (plate_format_id) REFERENCES plate_format(id)"]])]
   [(jdbc/create-table-ddl :layout_source_dest
                           [[:src :int "NOT NULL"]
                            [:dest :int "NOT NULL"]
                            ])]
   [(jdbc/create-table-ddl :plate_set
                           [[:id "SERIAL PRIMARY KEY"]
                             [:plate_set_name "varchar(250)"]
                            [:descr "varchar(250)"]
                            [:plate_set_sys_name "varchar(30)"]
                            [:num_plates :int ]
                            [:plate_format_id :int ]
                            [:plate_type_id :int ]
                            [:project_id :int ]
                            [:plate_layout_name_id :int ]
                            [:lnsession_id :int ]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
                            ["FOREIGN KEY (plate_type_id) REFERENCES plate_type(id)"]
                            ["FOREIGN KEY (plate_format_id) REFERENCES plate_format(id)"]
                            ["FOREIGN KEY (project_id) REFERENCES project(id) on delete cascade"]
                            ["FOREIGN KEY (lnsession_id) REFERENCES lnsession(id) on delete cascade"]
                            ["FOREIGN KEY (plate_layout_name_id) REFERENCES plate_layout_name(id)"]
                            ])]

    [(jdbc/create-table-ddl :plate
                           [[:id "SERIAL PRIMARY KEY"]
                            [:barcode "varchar(250)"]
                            [:plate_sys_name "varchar(30)"]                        
                            [:plate_type_id :int]
                            [:plate_format_id :int]
                            [:plate_layout_name_id :int]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]
                            ["FOREIGN KEY (plate_type_id) REFERENCES plate_type(id)"]
		            ["FOREIGN KEY (plate_format_id) REFERENCES plate_format(id)"]
		            ["FOREIGN KEY (plate_layout_name_id) REFERENCES plate_layout_name(id)"]   
                            ])]
     [(jdbc/create-table-ddl :plate_plate_set
                           [[:plate_set_id :int]
                            [:plate_id :int]
                            [:plate_order :int]
                            ["FOREIGN KEY (plate_set_id) REFERENCES plate_set(id)"]
		            ["FOREIGN KEY (plate_id) REFERENCES plate(id)"]
                           ])]
   [(jdbc/create-table-ddl :sample
                           [[:id "SERIAL PRIMARY KEY"]
                            [:sample_sys_name "varchar(30)"]
                            [:project_id :int]
                            [:accs_id "varchar(30)"]
                            ["FOREIGN KEY (project_id) REFERENCES project(id)"]
		           ])]
   [(jdbc/create-table-ddl :well
                           [[:id "SERIAL PRIMARY KEY"]
                            [:by_col :int]
                            [:plate_id :int]
                            ["FOREIGN KEY (plate_id) REFERENCES plate(id)"]
		           ])]
   [(jdbc/create-table-ddl :well_sample
                           [[:well_id :int]
                            [:sample_id :int]
                            ["FOREIGN KEY (well_id) REFERENCES well(id)"]
		            ["FOREIGN KEY (sample_id) REFERENCES sample(id)"]
		           ])]

    
  [(jdbc/create-table-ddl :assay_type
                           [[:id "SERIAL PRIMARY KEY"]
                            [:assay_type_name "varchar(250)"]
                            
                           ])]
  [(jdbc/create-table-ddl :assay_run
                          [[:id "SERIAL PRIMARY KEY"]
                           [:assay_run_sys_name "varchar(30)"]
                           [:assay_run_name "varchar(250)"]
                           [:descr "varchar(250)"]
                            [:assay_type_id :int]
                            [:plate_set_id :int]
                            [:plate_layout_name_id :int]
                            [:lnsession_id :int]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]                          
                           ["FOREIGN KEY (plate_set_id) REFERENCES plate_set(id)"]
                           ["FOREIGN KEY (plate_layout_name_id) REFERENCES plate_layout_name(id)"]
		           ["FOREIGN KEY (lnsession_id) REFERENCES lnsession(id)"]
		           ["FOREIGN KEY (assay_type_id) REFERENCES assay_type(id)"]
		           ])]

    [(jdbc/create-table-ddl :assay_result
                          [   [:assay_run_id :int]
                            [:plate_order :int]
                            [:well :int]
                           [:response :real]
                           [:bkgrnd_sub :real]
                           [:norm :real]
                           [:norm_pos :real]
                           [:p_enhance :real]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]                          
                           ["FOREIGN KEY (assay_run_id) REFERENCES assay_run(id)"]
                           ])]

       [(jdbc/create-table-ddl :assay_result_pre
                          [   [:assay_run_id :int]
                            [:plate_order :int]
                            [:well :int]
                           [:response :real]                        
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]                          
                           ["FOREIGN KEY (assay_run_id) REFERENCES assay_run(id)"]
                           ])]

    [(jdbc/create-table-ddl :hit_list
                          [[:id "SERIAL PRIMARY KEY"]
                           [:hitlist_sys_name "varchar(30)"]
                           [:hitlist_name "varchar(250)"]
                           [:descr "varchar(250)"]
                            [:n :int]
                           [:lnsession_id :int]
                           [:assay_run_id :int]
                            [:updated  :timestamp "with time zone not null DEFAULT current_timestamp"]               
                           ["FOREIGN KEY (lnsession_id) REFERENCES lnsession(id)"]
                           ["FOREIGN KEY (assay_run_id) REFERENCES assay_run(id)"]
		           ])]
  [(jdbc/create-table-ddl :hit_sample
                           [[:hitlist_id :int "NOT NULL"]
                            [:sample_id :int "NOT NULL"]
                            ["FOREIGN KEY (hitlist_id) REFERENCES hit_list(id)  ON DELETE cascade"]
                            ["FOREIGN KEY (sample_id) REFERENCES sample(id)  ON DELETE cascade"]
                            ])]

       [(jdbc/create-table-ddl :well_type
                               [[:id "SERIAL PRIMARY KEY"]
                             [:name "varchar(30)"]
                             ])]

     [(jdbc/create-table-ddl :plate_layout
                          [   [:plate_layout_name_id :int]
                            [:well_by_col :int]
                            [:well_type_id :int]
                           [:replicates :int]
                           [:target :int]                        
                           ["FOREIGN KEY (plate_layout_name_id) REFERENCES plate_layout_name(id)"]
                           ["FOREIGN KEY (well_type_id) REFERENCES well_type(id)"]
                           ])]
     
     [(jdbc/create-table-ddl :import_plate_layout
                          [   [:plate_layout_name_id :int]
                            [:well_by_col :int]
                            [:well_type_id :int]
                           [:replicates :int]
                           [:target :int]                        
                           ])]

         [(jdbc/create-table-ddl :temp_accs_id
                          [   [:plate_order :int]
                            [:by_col :int]
                            [:accs_id "varchar(30)"]
                           ])]
     
        [(jdbc/create-table-ddl :rearray_pairs
                                [[:id "SERIAL PRIMARY KEY"]
                                 [:src :int]
                                 [:dest :int]
                                 ])]
 
       [(jdbc/create-table-ddl :worklists
                          [   [:rearray_pairs_id :int]
                            [:sample_id :int]
                           [:source_plate "varchar(10)"]
                           [:source_well :int]
                           [:dest_plate "varchar(10)"]
                           [:dest_well :int]
                           ["FOREIGN KEY (rearray_pairs_id) REFERENCES rearray_pairs(id)  ON DELETE cascade"]
                           ["FOREIGN KEY (sample_id) REFERENCES sample(id)"]
                           ])]

       [(jdbc/create-table-ddl :well_numbers
                          [   [:plate_format :int]
                           [:well_name "varchar(5)"]
                            [:row "varchar(2)"]
                           [:row_num :int]
                           [:col "varchar(2)"]
                           [:total_col_count :int]
                           [:by_row :int]
                           [:by_col :int]
                           [:quad :int]
                           [:parent_well :int]
                           ])]
   ])


(def all-indices
  [["CREATE INDEX ON plate_layout_name(plate_format_id);"]
   ["CREATE INDEX ON plate(barcode);"]
   ["CREATE INDEX ON plate_set(plate_format_id);"]
   ["CREATE INDEX ON plate_set(plate_type_id);"]
   ["CREATE INDEX ON plate_set(project_id);"]
   ["CREATE INDEX ON plate_set(lnsession_id);"]
   ["CREATE INDEX ON plate(plate_type_id);"]
   ["CREATE INDEX ON plate(plate_format_id);"]
   ["CREATE INDEX ON plate_plate_set(plate_set_id);"]
   ["CREATE INDEX ON plate_plate_set(plate_id);"]
   ["CREATE INDEX ON project(lnsession_id);"]
   ["CREATE INDEX ON sample(project_id);"]
   ["CREATE INDEX ON well(plate_id);"]
   ["CREATE INDEX ON well_sample(well_id);"]
   ["CREATE INDEX ON well_sample(sample_id);"]
   ["CREATE INDEX ON assay_run(assay_type_id);"]
   ["CREATE INDEX ON assay_run(plate_set_id);"]
   ["CREATE INDEX ON assay_run(plate_layout_name_id);"]
   ["CREATE INDEX ON assay_run(lnsession_id);"]
   ["CREATE INDEX ON assay_result(assay_run_id);"]
   ["CREATE INDEX ON assay_result(plate_order);"]
   ["CREATE INDEX ON assay_result(well);"]
   ["CREATE INDEX ON hit_list(assay_run_id);"]
   ["CREATE INDEX ON hit_list(lnsession_id);"]
   ["CREATE INDEX ON hit_sample(hitlist_id);"]
   ["CREATE INDEX ON hit_sample(sample_id);"]
   ["CREATE INDEX ON plate_layout(plate_layout_name_id);"]
   ["CREATE INDEX ON plate_layout(well_type_id);"]
   ["CREATE INDEX ON plate_layout(well_by_col);"]
   ["CREATE INDEX ON temp_accs_id(plate_order);"]
   ["CREATE INDEX ON temp_accs_id(by_col);"]
   ["CREATE INDEX ON rearray_pairs(src);"]
   ["CREATE INDEX ON rearray_pairs(dest);"]
   ["CREATE INDEX ON well_numbers(by_col);"]
   ])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;Required data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn process-layout-data
  "processes that tab delimitted, R generated layouts for import
   order is important; must correlate with SQL statement order of ?'s"
  [x]
(into [] [ (Integer/parseInt(:id x)) (Integer/parseInt(:well x )) (Integer/parseInt(:type x )) (Integer/parseInt(:reps x )) (Integer/parseInt(:target x ))]))

(defn process-well-numbers-data
  "processes that tab delimitted, R generated well_numbers for import
because some are strings, all imported as string
   order is important; must correlate with SQL statement order of ?'s"
  [x]
  (into [] [ (Integer/parseInt (String. (:format x)))
            (:wellname x )
            (:row_name x )
            (Integer/parseInt (String. (:rownum x )))
            (Integer/parseInt (String. (:col x )))
            (Integer/parseInt (String. (:totcolcount x)))
            (Integer/parseInt (String. (:byrow x )))
            (Integer/parseInt (String. (:bycol x )))
            (Integer/parseInt (String. (:quad x )))
            (Integer/parseInt (String. (:parentwell x ))) ]))

(defn process-processed-assay-data
   "processes that tab delimitted, R generated well_numbers for import
because some are strings, all imported as string
   order is important; must correlate with SQL statement order of ?'s
:assay_run_id :plate_order :well :response :bkgrnd_sub :norm :norm_pos :p_enhance
assayid	PlateOrder	Well	Response	BkSub	Norm	NormPos	pEnhanced
"
  [x]
  (into [] [ (Integer/parseInt  (String. (:assay.id x)))
             (Integer/parseInt  (String. (:PlateOrder x)))
             (Integer/parseInt  (String. (:WellNum x)))
             (Double/parseDouble  (String. (:Response x)))
             (Double/parseDouble  (String. (:Bk_Sub x)))
             (Double/parseDouble  (String. (:Norm x)))
             (Double/parseDouble  (String. (:NormPos x)))
             (Double/parseDouble  (String. (:pEnhanced x)))]))


(def required-data
  ;;inserts required data into table using jdbc/insert-multi!
  ;;this is data that should not be deleted when repopulating with example data
  ;;this is data that is needed for basic functionality
  [[ :lnuser_groups 
    [ :usergroup ]
    [["administrator"]
     ["user" ]]]
                     
   [ :lnuser 
    [ :lnuser_name :tags :usergroup_id :password ]
    [["ln_admin" "ln_admin@labsolns.com" 1  "welcome"]
     ["ln_user" "ln_user@labsolns.com" 2 "welcome"]]]
   ;;  ["klohymim" "NA" 2 "hwc3v4_rbkT-1EL2KI-JBaqFq0thCXM_"]]]
   
   [ :plate_type [:plate_type_name]
    [["assay"]["rearray"]["master"]["daughter"]["archive"]["replicate"]]]
   
   [ :plate_format [:id :format :rownum :colnum]
    [[ 96, "96", 8, 12]
     [384, "384",16, 24]
     [1536, "1536", 32, 48]]]

   [ :plate_layout_name [:sys_name :name :descr :plate_format_id :replicates :targets :use_edge :num_controls :unknown_n :control_loc :source_dest]
    [["LYT-1" "4 controls col 12" "1S1T" 96 1 1 1 4 92 "E12-H12" "source"]
     ["LYT-2" "4 controls cols 23 24" "1S4T" 384  1 4 1 4 368 "I23-P24" "dest"]
     ["LYT-3" "4 controls cols 23 24" "2S2T" 384  2 2 1 4 368 "I23-P24" "dest"]
     ["LYT-4" "4 controls cols 23 24" "2S4T" 384  2 4 1 4 368 "I23-P24" "dest"]
     ["LYT-5" "4 controls cols 23 24" "4S1T" 384  4 1 1 4 368 "I23-P24" "dest"]
     ["LYT-6" "4 controls cols 23 24" "4S2T" 384  4 2 1 4 368 "I23-P24" "dest"]
     ["LYT-7" "8 controls col 12" "1S1T" 96  1 1 1 8 88 "A12-H12" "source"]
     ["LYT-8" "8 controls cols 23 24" "1S4T" 384  1 4 1 8 352 "A23-P24" "dest"]
     ["LYT-9" "8 controls cols 23 24" "2S2T" 384  2 2 1 8 352 "A23-P24" "dest"]
     ["LYT-10" "8 controls cols 23 24" "2S4T" 384  2 4 1 8 352 "A23-P24" "dest"]
 ["LYT-11" "8 controls cols 23 24" "4S1T" 384  4 1 1 8 352 "A23-P24" "dest"]
 ["LYT-12" "8 controls cols 23 24" "4S2T" 384  4 2 1 8 352 "A23-P24" "dest"]
 ["LYT-13" "8 controls col 24" "1S1T" 384  1 1 1 8 376 "I24-P24" "source"]
 ["LYT-14" "8 controls cols 47 48" "1S4T" 1536  1 4 1 8 1504 "Q47-AF48" "dest"]
 ["LYT-15" "8 controls cols 47 48" "2S2T" 1536  2 2 1 8 1504 "Q47-AF48" "dest"]
 ["LYT-16" "8 controls cols 47 48" "2S4T" 1536  2 4 1 8 1504 "Q47-AF48" "dest"]
 ["LYT-17" "8 controls cols 47 48" "4S1T" 1536  4 1 1 8 1504 "Q47-AF48" "dest"]
 ["LYT-18" "8 controls cols 47 48" "4S2T" 1536  4 2 1 8 1504 "Q47-AF48" "dest"]
 ["LYT-19" "16 controls col 24" "1S1T" 384  1 1 1 16 368 "A24-P24" "source"]
 ["LYT-20" "16 controls cols 47 48" "1S4T" 1536  1 4 1 16 1472 "A47-AF48" "dest"]
 ["LYT-21" "16 controls cols 47 48" "2S2T" 1536  2 2 1 16 1472 "A47-AF48" "dest"]
 ["LYT-22" "16 controls cols 47 48" "2S4T" 1536  2 4 1 16 1472 "A47-AF48" "dest"]
 ["LYT-23" "16 controls cols 47 48" "4S1T" 1536  4 1 1 16 1472 "A47-AF48" "dest"]
 ["LYT-24" "16 controls cols 47 48" "4S2T" 1536  4 2 1 16 1472 "A47-AF48" "dest"]
 ["LYT-25" "7 controls col 23" "1S1T" 384  1 1 0 7 301 "I23-O23" "source"]
 ["LYT-26" "7 controls cols 46 47" "1S4T" 1536  1 4 0 7 1204 "Q46-AE47" "dest"]
 ["LYT-27" "7 controls cols 46 47" "2S2T" 1536  2 2 0 7 1204 "Q46-AE47" "dest"]
 ["LYT-28" "7 controls cols 46 47" "2S4T" 1536  2 4 0 7 1204 "Q46-AE47" "dest"]
 ["LYT-29" "7 controls cols 46 47" "4S1T" 1536  4 1 0 7 1204 "Q46-AE47" "dest"]
 ["LYT-30" "7 controls cols 46 47" "4S2T" 1536  4 2 0 7 1204 "Q46-AE47" "dest"]
 ["LYT-31" "14 controls col 23" "1S1T" 384  1 1 0 14 294 "B23-O23" "source"]
 ["LYT-32" "14 controls cols 46 47" "1S4T" 1536  1 4 0 14 1176 "B46-AE47" "dest"]
 ["LYT-33" "14 controls cols 46 47" "2S2T" 1536  2 2 0 14 1176 "B46-AE47" "dest"]
 ["LYT-34" "14 controls cols 46 47" "2S4T" 1536  2 4 0 14 1176 "B46-AE47" "dest"]
 ["LYT-35" "14 controls cols 46 47" "4S1T" 1536  4 1 0 14 1176 "B46-AE47" "dest"]
 ["LYT-36" "14 controls cols 46 47" "4S2T" 1536  4 2 0 14 1176 "B46-AE47" "dest"]
 ["LYT-37" "8 controls cols 47 48" "1S1T" 1536  1 1 1 8 1504 "Q47-AF48" "source"]
 ["LYT-38" "16 controls cols 47 48" "1S1T" 1536  1 1 1 16 1472 "A47-AF48" "source"]
 ["LYT-39" "7 controls cols 46 47" "1S1T" 1536  1 1 0 7 1204 "Q46-AE47" "source"]
     ["LYT-40" "14 controls cols 46 47" "1S1T" 1536  1 1 0 14 1176 "B46-AE47" "source"]
     ["LYT-41" "all blanks" "not reformattable" 1536  1 1 0 0 0 "none" "dest"]]]

  [ :layout_source_dest [:src :dest ]
   [[1 2][1 3][1 4][1 5][1 6][7 8][7 9][7 10][7 11][7 12][13 14][13 15][13 16][13 17][13 18][19 20][19 21][19 22][19 23][19 24][25 26][25 27][25 28][25 29][25 30][31 32][31 33][31 34][31 35][31 36][37 41][38 41][39 41][40 41]]]

  [ :assay_type [:assay_type_name ]
   [["ELISA"]["Octet"]["SNP"]["HCS"]["HTRF"]["FACS"]]]

    [ :well_type [:name ]
     [["unknown"]["positive"]["negative"]["blank"]["edge"]]]
   
   [ :well_numbers [:plate_format :well_name :row :row_num :col :total_col_count :by_row :by_col :quad :parent_well ]
   ;; ln.data-sets/well-numbers
     (let   [  table (dbi/table-to-map (io/resource "data/well_numbers_for_import.txt"))
               content (into [] (map #(process-well-numbers-data %) table))]
       content)]
  

      [ :plate_layout [ :plate_layout_name_id :well_by_col :well_type_id :replicates :target]
      ;; ln.plate-layout-data/plate-layout-data
        (let   [  table (dbi/table-to-map (io/resource "data/plate_layouts_for_import.txt"))
               content (into [] (map #(process-layout-data %) table))]
         content)
    ]

   ])

(defn drop-all-tables
;;
[]
  (doall (map #(jdbc/db-do-commands cm/conn true  %) (map #(format  "DROP TABLE IF EXISTS %s CASCADE" %)  all-table-names ) )))



(defn initialize-limsnucleus
  ;;(map #(jdbc/db-do-commands cm/conn (jdbc/drop-table-ddl % {:conditional? true } )) all-table-names)
  []
  (doall (map #(jdbc/db-do-commands cm/conn true  %) (map #(format  "DROP TABLE IF EXISTS %s CASCADE" %)  all-table-names ) ))
 
  (doall (map #(jdbc/db-do-commands cm/conn true %) all-tables))
  (doall  (map #(jdbc/db-do-commands cm/conn true %) all-indices))


    ;; this errors because brackets not stripped
    ;;(map #(jdbc/insert-multi! cm/conn %) required-data)
  (doall  (map #(apply jdbc/insert-multi! cm/conn % ) required-data))
  (doall (map #(jdbc/db-do-commands cm/conn true  %) ln.db-functions/drop-all-functions))
  (doall (map #(jdbc/db-do-commands cm/conn true  %) ln.db-functions/all-functions))
  (if (not (c/is-open? cm/props)) (cm/open-or-create-props))
  (cm/set-init false))
 
;;(initialize-limsnucleus)

(defn add-example-data
  ;;
  []

  (doall (map #(jdbc/db-do-commands cm/conn true  %) ln.example-data/add-example-data-pre-assay))

  ; ;INSERT INTO assay_result (assay_run_id, plate_order, well, response) VALUES
;;assay_run_id | plate_order | well |  response  |  bkgrnd_sub  |     norm     |   norm_pos   | p_enhance 
  (jdbc/insert-multi! cm/conn :assay_result [:assay_run_id :plate_order :well :response :bkgrnd_sub :norm :norm_pos :p_enhance]
                      (let   [  table (dbi/table-to-map (io/resource "data/processed_data_for_import.txt"))
                              content (into [] (map #(process-processed-assay-data %) table))]
                        content) )

  (doall (map #(jdbc/db-do-commands cm/conn true  %) ln.example-data/add-example-data-post-assay))
  (cm/set-session-id 1)  )

(defn delete-example-data
  []
  (doall (map #(jdbc/db-do-commands cm/conn true  %) ln.example-data/delete-example-data)))


;;(add-example-data)
;;(println cm/conn)
