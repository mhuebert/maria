#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"release")
  echo "Deploying to www.maria.cloud:";
  npx firebase deploy -P prod;
  ;;
"master")
  echo "Deploying to dev.maria.cloud";
  npx firebase deploy -P dev;
  ;;
esac