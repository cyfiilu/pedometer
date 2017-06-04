create table 
	if not exists login(
	userid text PRIMARY KEY NOT NULL,
	username Text ,
    passworld Text,
    time INTEGER
);
create table
    IF NOT EXISTS user_info(
    userid Text,
    sex Text,
    city Text,
    height int,
    weight float,
    stepwidth int,
    daygoalnum int,
    zzgoalnum int,
    zzstarttime int,
    zzendtime int,
    mmgoalnum int,
    mmstarttime int,
    mmendtime int
);
create table
    if not exists user_step_data(
	userid text,
    daydate text,
	daystepcount int,
    daygoalnum int,
	km float,
	kcal int,
	zzstepcount int,
	zzgoalnum int,
    zzstarttime int,
    zzendtime int,
    mmstepcount int,
    mmgoalnum int,
    mmstarttime int,
    mmendtime int,
	hour0  int,
	hour1  int,
	hour2  int,
	hour3  int,
	hour4  int,
	hour5  int,
	hour6  int,
	hour7  int,
	hour8  int,
	hour9  int,
	hour10  int,
	hour11  int,
	hour12  int,
	hour13  int,
	hour14  int,
	hour15  int,
	hour16  int,
	hour17  int,
	hour18  int,
	hour19  int,
	hour20  int,
	hour21  int,
	hour22  int,
	hour23  int
);
create table
    IF NOT EXISTS step_sensor_info(
    userid Text,
    sensorcount int,
    sensortime text,
    boottime text
);
create table
    IF NOT EXISTS msg_add_friend(
    userid Text,
    friendname Text,
    reason Text,
    accepte Text,
    readtag Text,
    receivetime Text,
    headurl Text
);