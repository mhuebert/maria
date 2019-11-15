#!/usr/bin/env bash
case "$CIRCLE_BRANCH" in
"release")
  echo "Deploying to www.maria.cloud:";
  npx firebase deploy -P maria-d04a7 --token "$FIREBASE_PROD" --only hosting;
  ;;
"master")
  echo "Deploying to dev.maria.cloud";
  npx firebase deploy -P maria-dev --token "$FIREBASE_DEV" --only hosting;
  ;;
esac