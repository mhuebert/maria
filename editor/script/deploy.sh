#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"master")
  firebase deploy -P prod
  ;;
"dev")
  firebase deploy -P dev
  ;;
esac