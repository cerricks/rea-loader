SET sql_mode = '';

-- create indexed table based on gnaf.addr_txt_to_id_v
CREATE
  TABLE IF NOT EXISTS rea.addr_txt_to_id_tbl
SELECT
  address_detail_pid,
  address,
  state,
  post_code,
  locality
FROM
  gnaf.addr_txt_to_id_v;

CREATE INDEX gnaf_addr_idx ON rea.addr_txt_to_id_tbl (address, state, post_code, locality);
  
-- create indexed table based on gnaf.street_locality_v   
CREATE
  TABLE IF NOT EXISTS rea.street_locality_tbl
  (
    INDEX gnaf_loc_idx (state, post_code, locality, street_desc)
  )
SELECT
  street_locality_pid,
  state,
  post_code,
  locality,
  street_desc
FROM
  gnaf.street_locality_v;