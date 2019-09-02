(ns ln.example-data
  (:require ))

;;must be executed in order

 (def delete-example-data ["TRUNCATE project, plate_set, plate, hit_sample, hit_list, assay_run, assay_result, sample, well, lnsession RESTART IDENTITY CASCADE;"])

(def create-session ["INSERT INTO lnsession (lnuser_id) VALUES (1);"])



