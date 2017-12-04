create table if not exists tbl_user (
	user_name varchar(255) PRIMARY KEY,
	terminal_addr  varchar(128),
	regist_timestamp bigint
)
/

create table if not exists tbl_pf_calc_task (
	task_id varchar(1023) PRIMARY KEY,
	user_name varchar(255) NOT NULL,
	terminal_addr varchar(255) NOT NULL,
	state int default(0),
	num varchar(1023) NOT NULL,
	factors varchar(4095),
	begin_timestamp bigint,
	end_timestamp bigint
)
/

create table if not exists tbl_number (
	num varchar(1023) PRIMARY KEY
)