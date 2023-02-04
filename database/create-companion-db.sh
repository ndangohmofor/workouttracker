#!/usr/bin/env bash
BASEDIR=$(dirname $0)
psql -U postgres -f "$BASEDIR/dropdb.sql"
createdb -U postgres companion
psql -U postgres -d companion -f "$BASEDIR/schema.sql"
psql -U postgres -d companion -f "$BASEDIR/user.sql"
psql -U postgres -d companion -f "$BASEDIR/data.sql"
psql -U postgres -d companion -f "$BASEDIR/tables.sql"