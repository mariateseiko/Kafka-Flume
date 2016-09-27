CREATE EXTERNAL TABLE  logs_aux3 (
bid_id STRING,
tstamp STRING,
ipinyou_id STRING,
user_agent STRING,
ip STRING,
region INT,
city INT,
ad_exchange INT,
domain STRING,
url STRING,
anon_url_id STRING,
ad_slot_id STRING,
ad_slot_width INT,
ad_slot_height INT,
ad_slot_visibility INT,
ad_slot_format INT,
paying_price INT,
creative_id STRING,
bidding_price INT,
advertiser_id INT,
user_tags INT,
tags STRING) PARTITIONED BY (stream_id INT)
CLUSTERED BY (tstamp) INTO 32 BUCKETS
STORED AS ORC
LOCATION "/tmp/t"
TBLPROPERTIES ("transactional"="true")