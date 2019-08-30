(ns ln.db-functions
   )


(def drop-get-scatter-plot-data ["DROP FUNCTION IF exists get_scatter_plot_data( integer);"])

(def get-scatter-plot-data ["CREATE OR REPLACE FUNCTION get_scatter_plot_data(_assay_run_id INTEGER)
RETURNS TABLE(  plate INTEGER, well INTEGER, response REAL, bkgrnd_sub REAL,   norm REAL,   norm_pos REAL, p_enhance REAL,  well_type_id INTEGER,  replicates integer, target integer, sample_id integer ) AS
$BODY$
begin

CREATE TEMPORARY TABLE temp1 AS (SELECT  assay_result.plate_order,assay_result.well, assay_result.response, assay_result.bkgrnd_sub, assay_result.norm, assay_result.norm_pos, assay_result.p_enhance, assay_run.plate_set_id, assay_run.plate_layout_name_id, plate_layout.well_type_id, plate_layout.replicates, plate_layout.target FROM assay_run, assay_result JOIN plate_layout ON ( assay_result.well = plate_layout.well_by_col) WHERE assay_result.assay_run_id = assay_run.id  AND assay_run.ID = _assay_run_id AND plate_layout.plate_layout_name_id = assay_run.plate_layout_name_id);


CREATE TEMPORARY TABLE temp2 AS (SELECT plate_plate_set.plate_order, well.by_col, well_sample.sample_id FROM  plate_plate_set, plate_set, plate,  well,  well_sample, assay_run, sample WHERE plate_plate_set.plate_set_id = plate_set.ID AND plate_plate_set.plate_id = plate.ID AND well.plate_id = plate.id  and well_sample.well_id=well.ID AND well_sample.sample_id=sample.id AND plate_plate_set.plate_set_id = assay_run.plate_set_id AND assay_run.ID = _assay_run_id);


RETURN query
  SELECT temp1.plate_order,temp1.well, temp1.response, temp1.bkgrnd_sub, temp1.norm, temp1.norm_pos, temp1.p_enhance, temp1.well_type_id, temp1.replicates, temp1.target, temp2.sample_id FROM temp1 LEFT OUTER JOIN temp2 on (temp1.plate_order=temp2.plate_order AND temp1.well= temp2.by_col);

DROP TABLE temp1;
DROP TABLE temp2;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])





(def drop-create-layout-records ["DROP FUNCTION IF EXISTS create_layout_records(VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER, INTEGER, integer );"])

(def create-layout-records ["CREATE OR REPLACE FUNCTION create_layout_records(source_name VARCHAR, source_description VARCHAR, control_location VARCHAR, n_controls INTEGER, n_unknowns INTEGER, format INTEGER, n_edge integer)
 RETURNS void AS
$BODY$
DECLARE
   source_id INTEGER;
   dest_id INTEGER;
   edge INTEGER;
dest_layout_ids INTEGER[];
dest_layout_descr VARCHAR[] := '{\"1S4T\",\"2S2T\",\"2S4T\",\"4S1T\",\"4S2T\"}';
dest_format INTEGER;
i INTEGER;

BEGIN

IF n_edge >0 THEN edge = 0; ELSE edge = 1; END IF;

IF format = 96 THEN
dest_layout_ids := '{2,3,4,5,6}';
dest_format := 384;
END IF;
   
IF format = 384 THEN
dest_layout_ids := '{14,15,16,17,18}';
dest_format := 1536;
END IF;


INSERT INTO plate_layout_name (NAME, descr, plate_format_id, replicates, targets, use_edge, num_controls, unknown_n, control_loc, source_dest) VALUES (source_name, source_description, format, 1, 1, edge, n_controls, n_unknowns, control_location, 'source') RETURNING ID INTO source_id;

    UPDATE plate_layout_name SET sys_name = 'LYT-'|| source_id WHERE id=source_id;

--insert source
INSERT INTO plate_layout (SELECT source_id AS \"plate_layout_name_id\", well_by_col, well_type_id, replicates, target FROM import_plate_layout); 


--insert destinations
FOR i IN 1..5 loop
INSERT INTO plate_layout_name ( descr, plate_format_id, replicates, targets, use_edge, num_controls, unknown_n, control_loc, source_dest) VALUES ( dest_layout_descr[i], dest_format, 1, 1, edge, n_controls, n_unknowns, control_location, 'dest') RETURNING ID INTO dest_id;
 UPDATE plate_layout_name SET sys_name = 'LYT-'|| dest_id WHERE id=dest_id;

INSERT INTO plate_layout (SELECT dest_id AS \"plate_layout_name_id\", well_numbers.by_col AS \"well_by_col\", import_plate_layout.well_type_id, plate_layout.replicates, plate_layout.target FROM well_numbers, import_plate_layout, plate_layout WHERE well_numbers.plate_format = dest_format AND import_plate_layout.well_by_col=well_numbers.parent_well AND plate_layout.plate_layout_name_id=dest_layout_ids[i] AND plate_layout.well_by_col=well_numbers.by_col);


INSERT INTO layout_source_dest (src, dest) VALUES (source_id, dest_id);
END LOOP;
--


TRUNCATE TABLE import_plate_layout;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

   "])

(def drop-get-all-data-for-assay-run["DROP FUNCTION IF EXISTS get_all_data_for_assay_run(_assay_run_ids INTEGER );"])

(def get-all-data-for-assay-run["CREATE OR REPLACE FUNCTION get_all_data_for_assay_run(_assay_run_id INTEGER )
RETURNS TABLE(assay_run_sys_name VARCHAR,  plate_set_sys_name VARCHAR(32),  plate_sys_name VARCHAR(32), plate_order INT, well_name VARCHAR, type_well VARCHAR,  by_col INTEGER, response REAL, bkgrnd_sub REAL, norm REAL, norm_pos REAL, p_enhance REAL, sample_sys_name VARCHAR(32), accs_id VARCHAR, target INT)  AS
$BODY$
DECLARE

v_assay_run_id INTEGER := _assay_run_id;
v_plate_set_id INTEGER;
v_plate_format INTEGER;
v_layout_id INTEGER;

BEGIN

SELECT assay_run.plate_set_id FROM assay_run WHERE assay_run.ID =v_assay_run_id INTO v_plate_set_id;
SELECT plate_layout_name.plate_format_id FROM plate_layout_name, assay_run WHERE plate_layout_name.ID= assay_run.plate_layout_name_id AND assay_run.ID =v_assay_run_id INTO v_plate_format;
SELECT assay_run.plate_layout_name_id FROM assay_run WHERE assay_run.ID =v_assay_run_id INTO v_layout_id;



--get the plate set
CREATE TEMP TABLE plate_set_data(assay_run_sys_name VARCHAR, plate_set_sys_name VARCHAR, plate_sys_name VARCHAR, plate_order INT, well_name VARCHAR, type_well VARCHAR, by_col INT, well_id INT, response REAL, bkgrnd_sub REAL, norm REAL, norm_pos REAL, p_enhance REAL, target int );

INSERT INTO plate_set_data SELECT assay_run.assay_run_sys_name, plate_set.plate_set_sys_name , plate.plate_sys_name, plate_plate_set.plate_order, well_numbers.well_name, well_type.name, well.by_col, well.ID AS \"well_id\", assay_result.response, assay_result.bkgrnd_sub, assay_result.norm, assay_result.norm_pos, assay_result.p_enhance, plate_layout.target  FROM  plate_set, plate_plate_set, plate, well, assay_result, assay_run, well_numbers, plate_layout, well_type WHERE plate_plate_set.plate_set_id=plate_set.id AND plate_plate_set.plate_id=plate.ID and plate.id=well.plate_id  AND plate_set.ID = v_plate_set_id AND assay_result.assay_run_id= v_assay_run_id AND assay_result.plate_order=plate_plate_set.plate_order AND assay_result.well=well.by_col AND assay_run.ID = v_assay_run_id AND well_numbers.plate_format= v_plate_format AND well_numbers.by_col=well.by_col AND plate_layout.plate_layout_name_id=v_layout_id AND plate_layout.well_type_id=well_type.ID AND plate_layout.well_by_col=well.by_col ;

CREATE TEMP TABLE sample_names(well_id INT, sample_sys_name VARCHAR, accs_id VARCHAR);

INSERT INTO sample_names SELECT well.ID AS \"well_id\", sample.sample_sys_name, sample.accs_id  FROM well, well_sample, sample WHERE well_sample.sample_id=sample.ID AND well_sample.well_id=well.ID AND well.ID IN (SELECT well.ID FROM  plate_plate_set, plate, well WHERE plate_plate_set.plate_id = plate.ID AND well.plate_id = plate.ID AND plate_plate_set.plate_set_id = v_plate_set_id);

RETURN query
  SELECT  plate_set_data.assay_run_sys_name,  plate_set_data.plate_set_sys_name, plate_set_data.plate_sys_name, plate_set_data.plate_order, plate_set_data.well_name, plate_set_data.type_well ,  plate_set_data.by_col, plate_set_data.response, plate_set_data.bkgrnd_sub, plate_set_data.norm, plate_set_data.norm_pos, plate_set_data.p_enhance, sample_names.sample_sys_name, sample_names.accs_id, plate_set_data.target FROM plate_set_data LEFT JOIN sample_names on (plate_set_data.well_id=sample_names.well_id) ORDER BY plate_set_data.plate_order desc, plate_set_data.by_col DESC;

DROP TABLE plate_set_data;
DROP TABLE sample_names;

END;
$BODY$
LANGUAGE plpgsql VOLATILE;"] )




(def drop-all-functions
[   drop-get-scatter-plot-data  drop-create-layout-records drop-get-all-data-for-assay-run])

(def all-functions
  ;;for use in a map function that will create all functions
  ;;single command looks like:  (jdbc/drop-table-ddl :lnuser {:conditional? true } )
  [  get-scatter-plot-data  create-layout-records get-all-data-for-assay-run])

