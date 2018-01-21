#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"release")
  echo "Deploying to www.maria.cloud:";
  npx firebase deploy -P prod --token "$FIREBASE_TOKEN";
  ;;
"master")
  echo "Deploying to dev.maria.cloud";
  npx firebase deploy -P dev --token "$FIREBASE_TOKEN";
  ;;
esac