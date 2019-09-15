-- -*- mode: sql; sql-product: mysql; -*-

--https://postgres.cz/wiki/PostgreSQL_SQL_Tricks

-- assay_result:    assay_run_id  plate(plate_order)  well  response  bkgrnd_sub    norm   norm_pos 
-- assay_run:       id  assay_run_sys_name  assay_run_name  assay_type_id  plate_set_id  plate_layout_name_id
-- plate_layout:    plate_layout_name_id  well_by_col, well_type_id replicates  target
-- plate:           id plate_sys_name   plate_type_id   project_id   plate_format_id 
-- plate_set:       id   plate_set_name  plate_set_sys_name  num_plates  plate_format_id  plate_type_id  project_id  plate_layout_name_id
-- well:            id  plate_id  well_name
-- sample:          id
-- well_sample:     well_id  sample_id
-- well_type:       id  name
-- well_numbers:    plate_format  well_name  by_col quad 
-- plate_plate_set: plate_set_id   plate_id   plate_order
-- plate_layout_name  id  plate_format_id  replicates  targets  use_edge  num_controls control_loc source_dest 


SELECT * FROM project;

INSERT INTO well_sample (well_id, sample_id) VALUES ( 5186, (SELECT sample.id FROM sample, well, well_sample WHERE well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.id= 4788));




SELECT sample.id FROM sample, well, well_sample WHERE well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.id= 4788;

SELECT * FROM sample; --4640
SELECT * FROM well_sample WHERE sample_id=4640; --well4788
SELECT * FROM well_sample WHERE sample_id= null;


SELECT * FROM well LIMIT 5;
INSERT INTO well (by_col, plate_id) VALUE (97,1);
SELECT * FROM well WHERE by_col=97;  --id 5186

SELECT * FROM assay_run;

select well.plate_id, plate_plate_set.plate_order, well.by_col, well.id AS source_well_id, sample.id AS sample_id  FROM plate_plate_set, well, sample, well_sample  WHERE plate_plate_set.plate_set_id = 3 AND plate_plate_set.plate_id = well.plate_id AND well_sample.well_id=well.id AND well_sample.sample_id=sample.id ORDER BY well.plate_id, well.ID;

SELECT sample.id FROM sample, well, well_sample WHERE well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.id= ?)

SHOW TABLES;

CREATE TABLE plate_layout_name (id SERIAL PRIMARY KEY, sys_name varchar(30), name varchar(250), descr varchar(250), plate_format_id int, replicates int, targets int, use_edge int, num_controls int, unknown_n int, control_loc varchar(30), source_dest varchar(30), FOREIGN KEY (plate_format_id) REFERENCES plate_format(id));
