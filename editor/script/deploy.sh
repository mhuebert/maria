#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"release")
  npx firebase deploy -P prod
  ;;
"master")
  npx firebase deploy -P dev
  ;;
esac