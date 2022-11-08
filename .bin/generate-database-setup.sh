#!/bin/bash

set -u -e

q=$(printf '$q%x$\n' ${RANDOM})

echo "CREATE USER \"${DATABASE_USER}\" WITH PASSWORD ${q}${DATABASE_PASSWORD}${q}";
echo ";"
echo "CREATE DATABASE \"${DATABASE_NAME}\" WITH OWNER \"${DATABASE_USER}\"";
echo ";"

