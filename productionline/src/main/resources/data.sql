-- Create stations
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery) VALUES ('Station', '75583b61-db90-4a96-b5b5-a5c88ffbcffa', 5, 0.01, 'Painting', 10);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery) VALUES ('Station', 'd57af59a-e45d-494d-8642-8c4114b0ba1b', 15, 0.1, 'Wheels', 20);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery) VALUES ('Station', 'bcf43ebc-7d68-42a9-9302-47fec176c877', 25, 0.2, 'Body parts', 15);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery) VALUES ('Station', '3bc0efbe-710e-46dd-9cb2-70e8f0620d98', 10, 0.005, 'Windshield', 5);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery) VALUES ('Station', '1225367e-1cad-45e4-b1f5-51a638514a3e', 1, 0.5, 'Bumpers', 1);

-- Create robots
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery, maintenance_cycle_in_minutes, maintenance_time_in_minutes) VALUES ('Robot', '4ea844bd-14a1-45a8-b9f7-5956b8e1dfa7', 2, 0.2, 'Painting robot', 10, 20, 5);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery, maintenance_cycle_in_minutes, maintenance_time_in_minutes) VALUES ('Robot', '1322acdf-04b9-4900-b864-11fd02f6c0c1', 10, 0.1, 'Wheel robot', 10, 30, 10);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery, maintenance_cycle_in_minutes, maintenance_time_in_minutes) VALUES ('Robot', '1e7f95ba-4221-481e-8604-20a8fa23d008', 10, 0.05, 'Ratchet robot', 5, 120, 10);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery, maintenance_cycle_in_minutes, maintenance_time_in_minutes) VALUES ('Robot', 'eb93f95a-357f-425f-8e77-56a42d3576c3', 10, 0.02, 'Your mom is a robot', 15, 15, 5);
INSERT INTO production_step (dtype, uuid, duration_in_minutes, failure_probability, name, time_to_recovery, maintenance_cycle_in_minutes, maintenance_time_in_minutes) VALUES ('Robot', 'c3290836-0636-4046-95db-5a472b12aaf5', 10, 0.5, 'Good luck robot', 1, 20, 3);

-- Create employees
INSERT INTO employee (uuid, name, station_uuid) VALUES ('5d3dbfad-c6f4-44da-b4b2-638a7a7ebd7f', 'Janne', '75583b61-db90-4a96-b5b5-a5c88ffbcffa');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('3d1c04fc-4a9e-4103-a773-a163b15a18a7', 'Adriana', '75583b61-db90-4a96-b5b5-a5c88ffbcffa');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('061828e9-3319-4332-8181-9fbefc23a1cc', 'Chris', '75583b61-db90-4a96-b5b5-a5c88ffbcffa');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('f659f7af-5db7-478c-95ca-592eac07107c', 'Tim', '75583b61-db90-4a96-b5b5-a5c88ffbcffa');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('9af71323-21be-49be-af9a-556a1e1f5a11', 'Leon', '75583b61-db90-4a96-b5b5-a5c88ffbcffa');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('6420bdc4-0a98-4ced-979d-25dc84ad8bd1', 'Norma', 'bcf43ebc-7d68-42a9-9302-47fec176c877');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('bc2ef023-a965-4b1a-8303-25d889994a8f', 'Sandy', 'bcf43ebc-7d68-42a9-9302-47fec176c877');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('c2501544-5955-47f5-a04b-3e8b40c89e15', 'Jen', 'bcf43ebc-7d68-42a9-9302-47fec176c877');
INSERT INTO employee (uuid, name, station_uuid) VALUES ('dbef6306-d92b-4bd8-98b6-eff11c5effb9', 'Amelia', null);
INSERT INTO employee (uuid, name, station_uuid) VALUES ('b41e7310-cd0b-4931-a38d-d75ab6097672', 'Hartmut', null);
INSERT INTO employee (uuid, name, station_uuid) VALUES ('6a845542-f312-49ef-a30c-1b30aa544214', 'Penny', null);

-- Create production lines