#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"release")
  echo "Deploying to www.maria.cloud:";
  npx firebase deploy -P "$FIREBASE_PROD" --only hosting;
  ;;
"master")
  echo "Deploying to dev.maria.cloud";
  npx firebase deploy -P "$FIREBASE_DEV" --only hosting;
  ;;
esac