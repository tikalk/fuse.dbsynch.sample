
XPR_DEBUG_PORT=8002
export XPR_DEBUG_PORT
SUSPEND_UNTIL_DEBUG_ATTACHE=y
export SUSPEND_UNTIL_DEBUG_ATTACHE

./bin/server.sh "$@"