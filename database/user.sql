-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER companion_owner WITH PASSWORD 'companion_owner@1982';

GRANT ALL ON ALL TABLES IN SCHEMA public TO companion_owner;

GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO companion_owner;

CREATE USER companion_appuser WITH PASSWORD 'companion_appuser@1982';

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA  public TO companion_appuser;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA  public TO companion_appuser;