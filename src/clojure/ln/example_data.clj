(ns ln.example-data)

;;must be executed in order; reset resets the index counter

(def delete-example-data ["TRUNCATE project, plate_set, plate, hit_sample, hit_list, assay_run, assay_result, sample, well, lnsession, target, target_layout, target_layout_name RESTART IDENTITY CASCADE;"])

(def create-session ["INSERT INTO lnsession (lnuser_id) VALUES (1);"]) 

(def add-projects [
                   "SELECT new_project('3 plate sets with 2 96 well plates each', 'With AR, HL', 1 );"
                   "SELECT new_project('1 plate set with 2 384 well plates each', 'With AR', 1 );"
                   "SELECT new_project('1 plate set with 1 1536 well plate', 'With AR', 1 );"
                   "SELECT new_project('description 4', 'MyTestProj4', 1 );"
                   "SELECT new_project('description 5', 'MyTestProj5', 1 );"
                   "SELECT new_project('description 6', 'MyTestProj6', 1 );"
                   "SELECT new_project('description 7', 'MyTestProj7', 1 );"
                   "SELECT new_project('description 8', 'MyTestProj8', 1 );"
                   "SELECT new_project('description 9', 'MyTestProj9', 1 );"
                   "SELECT new_project('2 plate sets with 10 96 well plates each', 'Plates only, no data', 1 );"
                   ])

(def add-plate-sets [
            "SELECT new_plate_set('with AR (low values), HL','2 96 well plates',2,96,1,1,1,1,TRUE,NULL);"
            "SELECT new_plate_set('with AR (low values), HL','2 96 well plates',2,96,1,1,1,1,TRUE,NULL);"
            "SELECT new_plate_set('with AR (high values), HL','2 96 well plates',2,96,1,1,1,1,TRUE,NULL);"
            "SELECT new_plate_set('with AR (low values), HL','2 384 well plates',2,384,1,2,13,1,TRUE,NULL);"
            "SELECT new_plate_set('with AR (low values), HL','1 1536 well plate',1, 1536, 1, 3, 37, 1,TRUE,NULL);"
            "SELECT new_plate_set('using LYT-1/;96/;4in12','Plates only',10,96,1,10,1,1,TRUE,NULL);"
             "SELECT new_plate_set('using LYT-1/;96/;4in12','Plates only',10,96,1,10,1,1,TRUE,NULL);"
                     ])

(def add-assay-runs [

                     "select new_assay_run( 'assay_run1', 'PS-1 LYT-1;96;4in12', 1, 1, 1,1);"         
                     "select new_assay_run( 'assay_run2', 'PS-2 LYT-1/;96/;4in12', 1, 2, 1,1);"
                     "select new_assay_run( 'assay_run3', 'PS-3 LYT-1/;96/;4in12', 5, 3, 1,1);"
                     "select new_assay_run( 'assay_run4', 'PS-4 LYT-13/;384/;8in24', 1, 4, 13, 1);"
                     "select new_assay_run( 'assay_run5', 'PS-5 LYT-37/;1536/;32in47,48', 1, 5, 37, 1);"
                     ])


(def add-hit-lists [
                    "SELECT new_hit_list('hit list 1', 'descr1', 10, 1, 1,  ARRAY[87, 39, 51, 59, 16, 49, 53, 73, 65, 43]);"
                    "SELECT new_hit_list('hit list 2', 'descr2', 20, 1, 1,  ARRAY[154, 182, 124, 172, 171, 164, 133, 155, 152, 160, 118, 93, 123, 142, 183, 145, 95, 120, 158, 131]);"
                    "SELECT new_hit_list('hit list 3', 'descr3', 10, 2, 1,  ARRAY[216, 193, 221, 269, 244, 252, 251, 204, 217, 256]);"
                    "SELECT new_hit_list('hit list 4', 'descr4', 20, 2, 1,  ARRAY[311, 277, 357, 314, 327, 303, 354, 279, 346, 318, 344, 299, 355, 300, 325, 290, 278, 326, 282, 334]);"
                    "SELECT new_hit_list('hit list 5', 'descr5', 10, 3, 1, ARRAY[410, 412, 393, 397, 442, 447, 428, 374, 411, 437]);"
                    "SELECT new_hit_list('hit list 6', 'descr6', 20, 3, 1, ARRAY[545, 514, 479, 516, 528, 544, 501, 472, 463, 494, 531, 482, 513, 468, 465, 510, 535, 478, 502, 488]);"
                 ])

;; target related also used during no example-data init
(def add-target-related [
                         "SELECT new_target(  NULL, 'Target1', 'Generic Target 1', NULL);"
                         "SELECT new_target(  NULL, 'Target2', 'Generic Target 2', NULL);"
                         "SELECT new_target(  NULL, 'Target3', 'Generic Target 3', NULL);"
                         "SELECT new_target(  NULL, 'Target4', 'Generic Target 4', NULL);"
                         "SELECT new_target_layout_name ( NULL, 'DefaultSinglicates', 'Generic default target layout for singlicates',1,1,2,3,4);"
                         "SELECT new_target_layout_name ( NULL, 'DefaultDuplicates', 'Generic default target layout for duplicates',2,1,2,3,4);"
                         "SELECT new_target_layout_name ( NULL, 'DefaultQuadruplicates', 'Generic default target layout for quadruplicates',4,1,2,3,4);"])


(def add-example-data-pre-assay [ delete-example-data create-session add-projects add-plate-sets add-assay-runs ])

(def add-example-data-post-assay [  add-hit-lists add-target-related ])
















