(ns ln.db-functions
   )

(def drop-new-plate-set ["DROP FUNCTION IF exists new_plate_set(_descr VARCHAR(30), _plate_set_name VARCHAR(30), _num_plates INTEGER, _plate_format_id INTEGER,  _plate_type_id INTEGER, _project_id INTEGER, _plate_layout_name_id INTEGER, _lnsession_id INTEGER,  _with_samples boolean);"])


(def new-plate-set ["CREATE OR REPLACE FUNCTION new_plate_set(_descr VARCHAR(30),_plate_set_name VARCHAR(30), _num_plates INTEGER, _plate_format_id INTEGER, _plate_type_id INTEGER, _project_id INTEGER, _plate_layout_name_id INTEGER, _lnsession_id INTEGER, _with_samples boolean)
  RETURNS integer AS
$BODY$
DECLARE
   ps_id INTEGER;
   n_plates INTEGER;
   p_type INTEGER;
   p_form INTEGER;
   prj_id INTEGER;
   plt_id INTEGER;
   play_n_id INTEGER;
   w_spls BOOLEAN := _with_samples;
BEGIN
   
   INSERT INTO plate_set(descr, plate_set_name, num_plates, plate_format_id, plate_type_id, project_id, plate_layout_name_id, lnsession_id)
   VALUES (_descr, _plate_set_name, _num_plates, _plate_format_id, _plate_type_id, _project_id, _plate_layout_name_id, _lnsession_id )
   RETURNING ID, plate_format_id, num_plates, project_id, plate_type_id, plate_layout_name_id INTO ps_id, p_form, n_plates, prj_id, p_type, play_n_id;
   UPDATE plate_set SET plate_set_sys_name = 'PS-'||ps_id WHERE id=ps_id;

FOR i IN 1..n_plates loop
	     -- _plate_type_id INTEGER, _plate_set_id INTEGER, _project_id INTEGER, _plate_format_id INTEGER, include_sample BOOLEAN
	    SELECT new_plate(p_type, ps_id, p_form, play_n_id, w_spls) INTO plt_id;
	    UPDATE plate_plate_set SET plate_order = i WHERE plate_set_id = ps_id AND plate_id = plt_id;

END LOOP;

RETURN ps_id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])


(def drop-new-plate ["DROP FUNCTION IF EXISTS new_plate(INTEGER, INTEGER,INTEGER,INTEGER, BOOLEAN);"])

(def new-plate ["CREATE OR REPLACE FUNCTION new_plate(_plate_type_id INTEGER, _plate_set_id INTEGER, _plate_format_id INTEGER, _plate_layout_name_id INTEGER,  _include_sample BOOLEAN)
  RETURNS integer AS
$BODY$
DECLARE
   plt_id INTEGER;
   ps_id INTEGER = _plate_set_id;
   pf_id INTEGER;
   w_id INTEGER;
   s_id INTEGER;
   spl_include BOOLEAN := _include_sample;
   w integer;	

BEGIN

   INSERT INTO plate(plate_type_id,   plate_format_id, plate_layout_name_id)
   VALUES (_plate_type_id,  _plate_format_id, _plate_layout_name_id)
   RETURNING id  INTO plt_id;

    UPDATE plate SET plate_sys_name = 'PLT-'||plt_id WHERE id=plt_id;


FOR w IN 1.._plate_format_id LOOP

  --RAISE notice 'w: (%)', w;
       INSERT INTO well(by_col, plate_id) VALUES(w, plt_id)
       RETURNING id INTO w_id;
       
       IF spl_include THEN  --check if it is an \"unknown\" well i.e. not a control
       IF w IN (SELECT well_by_col  FROM plate_layout, plate_layout_name  WHERE plate_layout.plate_layout_name_id = plate_layout_name.id AND plate_layout.well_type_id = 1 AND plate_layout.plate_layout_name_id = _plate_layout_name_id) THEN
       INSERT INTO sample (sample_sys_name) VALUES (null)
       RETURNING id INTO s_id;
       UPDATE sample SET sample_sys_name = 'SPL-'||s_id WHERE id=s_id;

       INSERT INTO well_sample(well_id, sample_id)VALUES(w_id, s_id);
      END IF;
       END IF;
   END LOOP;

   INSERT INTO plate_plate_set(plate_set_id, plate_id)
   VALUES (ps_id, plt_id );

RETURN plt_id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])



(def drop-new-sample ["DROP FUNCTION IF EXISTS new_sample(INTEGER,INTEGER,INTEGER);"])

(def new-sample ["CREATE OR REPLACE FUNCTION new_sample(_project_id INTEGER, _plate_id INTEGER,  _accs_id INTEGER)
  RETURNS void AS
$BODY$
DECLARE
   v_id integer;
BEGIN
   
   INSERT INTO sample(project_id, plate_id, accs_id)
   VALUES (_project_id, _plate_id,   _accs_id)
   RETURNING id INTO v_id;

    UPDATE sample SET sample_sys_name = 'SPL-'||v_id WHERE id=v_id RETURNING id;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])


(def drop-new-plate-layout ["DROP FUNCTION IF EXISTS new_plate_layout(  VARCHAR(30), VARCHAR(30), INTEGER,  VARCHAR[][]);"])

(def new-plate-layout ["CREATE OR REPLACE FUNCTION new_plate_layout( _plate_layout_name VARCHAR(30), _descr VARCHAR(30), _plate_format_id INTEGER, _data VARCHAR[][])
  RETURNS integer AS
$BODY$
DECLARE
   src_id integer;
   dest_id INTEGER;
   dest_name VARCHAR(30);
BEGIN


   INSERT INTO plate_layout_name(name, descr, plate_format_id)
   VALUES (_plate_layout_name, _descr, _plate_format_id)
   RETURNING id INTO src_id;

dest_name := _plate_layout_name || '-dest';

   INSERT INTO plate_layout_name(name, descr, plate_format_id)
   VALUES (_plate_layout_name, _descr, _plate_format_id)
   RETURNING id INTO dest_id;

INSERT INTO layout_source_dest(src, dest) VALUES (src_id, dest_id);


 
RETURN plname_id;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])


(def drop-reformat-plate-set ["DROP FUNCTION IF exists reformat_plate_set(source_plate_set_id INTEGER, source_num_plates INTEGER, n_reps_source INTEGER, dest_descr VARCHAR(30), dest_plate_set_name VARCHAR(30), dest_num_plates INTEGER, dest_plate_format_id INTEGER, dest_plate_type_id INTEGER, project_id INTEGER, dest_plate_layout_name_id INTEGER );"])

(def reformat-plate-set ["CREATE OR REPLACE FUNCTION reformat_plate_set(source_plate_set_id INTEGER, source_num_plates INTEGER, n_reps_source INTEGER, dest_descr VARCHAR(30), dest_plate_set_name VARCHAR(30), dest_num_plates INTEGER, dest_plate_format_id INTEGER, dest_plate_type_id INTEGER, project_id INTEGER, dest_plate_layout_name_id INTEGER )
RETURNS integer AS
$BODY$
DECLARE

dest_plate_set_id INTEGER;
all_source_well_ids INTEGER[];
all_dest_well_ids INTEGER[];
w INTEGER;
holder INTEGER;

BEGIN
--here I am creating the destination plate set, no samples included
SELECT new_plate_set(dest_descr ,dest_plate_set_name, dest_num_plates, dest_plate_format_id, dest_plate_type_id, project_id, dest_plate_layout_name_id, lnsession_id, false) INTO dest_plate_set_id;

--RAISE notice 'dest_plate_set_id: (%)', dest_plate_set_id;

CREATE TEMP TABLE temp1(counter INT, plate_id INT, plate_order INT, well_by_col INT, well_id INT);

FOR i IN 1..n_reps_source LOOP
INSERT INTO temp1 select i, well.plate_id, plate_plate_set.plate_order, well.by_col, well.id AS well_id FROM plate_plate_set, well  WHERE plate_plate_set.plate_set_id = source_plate_set_id AND plate_plate_set.plate_id = well.plate_id   ORDER BY well.plate_id, well.ID;
END LOOP;

SELECT ARRAY (SELECT well_id FROM temp1 ORDER BY plate_id, counter, well_id) INTO all_source_well_ids;


SELECT ARRAY (SELECT  dest.id  FROM ( SELECT plate_plate_set.plate_ID, well.by_col,  well.id  FROM well, plate_plate_set  WHERE plate_plate_set.plate_set_id = dest_plate_set_id  AND plate_plate_set.plate_id = well.plate_id) AS dest JOIN (SELECT well_numbers.well_name, well_numbers.by_col, well_numbers.quad FROM well_numbers WHERE well_numbers.plate_format=dest_plate_format_id)  AS foo ON (dest.by_col=foo.by_col) ORDER BY plate_id, quad, dest.by_col) INTO all_dest_well_ids;


FOR w IN 1..array_length(all_source_well_ids,1)  LOOP
SELECT sample.id FROM sample, well, well_sample WHERE well_sample.well_id=well.id AND well_sample.sample_id=sample.id AND well.id= all_source_well_ids[w] INTO holder;
INSERT INTO well_sample (well_id, sample_id) VALUES (all_dest_well_ids[w], holder );


--RAISE notice  'w: (%)', w;
--RAISE notice  'all_source_well_ids[w]: (%)', all_source_well_ids[w];
--RAISE notice  'all_dest_well_ids[w]: (%)', all_dest_well_ids[w];

END LOOP;

--RAISE notice  'all_source_well_ids: (%)', all_source_well_ids;
--RAISE notice  'all_dest_well_ids: (%)', all_dest_well_ids;


DROP TABLE temp1;

RETURN dest_plate_set_id;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;"])




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

(def drop-new-hit-list ["DROP FUNCTION IF EXISTS new_hit_list(_name VARCHAR(250), _descr VARCHAR(250), _num_hits INTEGER, _assay_run_id INTEGER, _lnsession_id INTEGER, hit_list integer[]);"])

(def new-hit-list ["CREATE OR REPLACE FUNCTION new_hit_list(_name VARCHAR(250), _descr VARCHAR(250), _num_hits INTEGER, _assay_run_id INTEGER, _lnsession_id INTEGER, hit_list integer[])
  RETURNS void AS
$BODY$
DECLARE
 hl_id INTEGER;
 hl_sys_name VARCHAR(10);
 s_id INTEGER;
BEGIN


  INSERT INTO hit_list(hitlist_name, descr, n, assay_run_id, lnsession_id)
   VALUES (_name, _descr, _num_hits, _assay_run_id, _lnsession_id)
   RETURNING id INTO hl_id;

    UPDATE hit_list SET hitlist_sys_name = 'HL-'|| hl_id WHERE id=hl_id;
    
FOR i IN 1.._num_hits loop
 INSERT INTO hit_sample(hitlist_id, sample_id)VALUES(hl_id, hit_list[i]);
END LOOP;

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



(def drop-rearray-transfer-samples ["DROP FUNCTION IF EXISTS rearray_transfer_samples(integer, INTEGER, integer);"])


(def rearray-transfer-samples ["CREATE OR REPLACE FUNCTION rearray_transfer_samples(source_plate_set_id INTEGER, dest_plate_set_id INTEGER, hit_list_id integer)
 RETURNS void AS
$BODY$
DECLARE
   i INTEGER;
all_hit_sample_ids INTEGER[];
dest_wells INTEGER[];
num_hits INTEGER;
rp_id INTEGER;


BEGIN
--select get in plate, well order, not necessarily sample order 
SELECT ARRAY (SELECT  sample.id FROM plate_set, plate_plate_set, plate, well, well_sample, sample WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id=source_plate_set_id AND sample.ID  IN  (SELECT hit_sample.sample_id FROM hit_sample WHERE hit_sample.hitlist_id = hit_list_id) ORDER BY plate.ID, well.ID) INTO all_hit_sample_ids;

num_hits := array_length(all_hit_sample_ids, 1);
--raise NOTice 'num_hits: (%)', num_hits;

SELECT ARRAY (SELECT well.ID FROM plate_set, plate_plate_set, plate, well, plate_layout WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND plate_set.plate_layout_name_id=plate_layout.plate_layout_name_id AND plate_layout.well_by_col= well.by_col AND plate_set.id=dest_plate_set_id AND plate_layout.well_type_id=1 ORDER BY well.ID) INTO dest_wells;


  for i IN 1..num_hits
  loop
  INSERT INTO well_sample (well_id, sample_id) VALUES ( dest_wells[i], all_hit_sample_ids[i]);   
raise NOTice 'dest_well: (%)', dest_wells[i];
raise NOTice 'sample: (%)', all_hit_sample_ids[i];


END LOOP;

INSERT INTO rearray_pairs (src, dest) VALUES (source_plate_set_id, dest_plate_set_id)  returning id INTO rp_id;

CREATE TEMP TABLE temp1 (plate_sys_name VARCHAR(10), by_col INTEGER, sample_id INTEGER);

INSERT INTO temp1 SELECT  plate.plate_sys_name, well.by_col, sample.ID AS \"sample_id\"  FROM plate_set, plate_plate_set, plate, well, well_sample, sample  WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id=source_plate_set_id  AND sample.ID IN  (SELECT hit_sample.sample_id FROM hit_sample WHERE hit_sample.hitlist_id = hit_list_id ORDER BY sample.ID);

CREATE TEMP TABLE temp2 (plate_sys_name VARCHAR(10), by_col INTEGER, sample_id INTEGER);

INSERT INTO temp2 SELECT  plate.plate_sys_name, well.by_col, sample.ID AS \"sample_id\" FROM plate_set, plate_plate_set, plate, well, well_sample, sample  WHERE plate_plate_set.plate_set_id=plate_set.ID AND plate_plate_set.plate_id=plate.id AND well.plate_id=plate.ID AND well_sample.well_id=well.ID AND well_sample.sample_id=sample.ID and plate_set.id=dest_plate_set_id  ORDER BY sample.ID;

INSERT INTO worklists ( rearray_pairs_id, sample_id, source_plate, source_well, dest_plate, dest_well) SELECT rp_id, temp1.sample_id, temp1.plate_sys_name, temp1.by_col, temp2.plate_sys_name, temp2.by_col FROM temp1, temp2 WHERE temp1.sample_id = temp2.sample_id;

DROP TABLE temp1, temp2;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;"])


(def drop-all-functions
[ drop-new-plate-set  drop-new-plate drop-new-sample  drop-new-plate-layout drop-reformat-plate-set  drop-get-scatter-plot-data drop-new-hit-list  drop-rearray-transfer-samples drop-create-layout-records drop-get-all-data-for-assay-run])

(def all-functions
  ;;for use in a map function that will create all functions
  ;;single command looks like:  (jdbc/drop-table-ddl :lnuser {:conditional? true } )
  [ new-plate-set new-plate new-sample  new-plate-layout reformat-plate-set get-scatter-plot-data new-hit-list  rearray-transfer-samples create-layout-records get-all-data-for-assay-run])

