#!/bin/bash
set -e

if [ -z "${SCM_TAG}" ] || [ -z "${ORGANIZATION}" ] || [ -z "${EMAIL}" ] || [ -z "${TOKEN}" ] || [ -z "${LANGUAGE}" ] ; then
    echo "You have to provide all the mandatory information in the infrabox job environment"
    exit 1;
fi

if [ -z "${PATH_TO_SOURCE_CODE}" ]; then
    PATH_TO_SOURCE_CODE="./"
fi

echo "Downloading coverity tools..."
wget https://scan.coverity.com/download/linux64 --post-data "token=${TOKEN}&project=${ORGANIZATION}%2F${PROJECT}" -O coverity_tool.tgz && tar zxf coverity_tool.tgz && rm coverity_tool.tgz && mv cov-analysis-linux64-* cov-analysis-linux64
echo "done"

#TODO scan files from /infrabox/context
echo "Cloning git repository..."
git clone https://github.com/${ORGANIZATION}/${PROJECT}.git
cd ${PROJECT}
echo "done"

exec $CLEAN_UP_STEP

echo "launching cov-build, this will take at least 30 min"
case $LANGUAGE in
    c | cpp)
    LANGAGE_SETUP="-DskipTests=true ${BUILD_COMMAND}"
    ;;
    java_maven)
    LANGAGE_SETUP="mvn -DskipTests=true -Dfindbugs.skip=true compile"
    ;;
    java_ant)
    LANGAGE_SETUP="ant -DskipTests=true -Dfindbugs.skip=true compile"
    ;;
    csharp)
    LANGAGE_SETUP=$BUILD_COMMAND
    ;;
#    js_alone)
#    LANGAGE_SETUP="--no-command --fs-capture-search $PATH_TO_SOURCE_CODE"
#    ;;
    js_java_or_csharp)
    LANGAGE_SETUP="--fs-capture-search $PATH_TO_SOURCE_CODE $BUILD_COMMAND"
    ;;
    *)
    LANGAGE_SETUP="--no-command --fs-capture-search $PATH_TO_SOURCE_CODE"
    ;;
esac
echo $(ls cov-analysis-linux64/bin)
cov-build --dir cov-int $LANGAGE_SETUP

# Prepare the submission archive
echo "Archiving cov-int.tgz"
tar czvf cov-int.tgz cov-int

#TODO remove this if the upload to coverity works
cp -r cov-int /infrabox/upload/archive/cov-int

# Upload to coverity
#FIXME Access Denied
DESTINATION_URL="https://scan.coverity.com/builds?project=${ORGANIZATION}%2F${PROJECT}"
echo "Uploading results to ${DESTINATION_URL}"
curl --form token="${TOKEN}" \
  --form email="${EMAIL}" \
  --form file=@cov-int.tgz \
  --form version="${SCM_TAG}" \
  --form description="Automatic Coverity Scan build for ${SCM_TAG}" \
  ${DESTINATION_URL}

#Adding the Coverity section with link to the results url
cp checkmarxreport.json /infrabox/upload/markup/checkmarx.json
URL=https://scan.coverity.com/projects/${TOKEN}-${PROJECT}
sed -i 's@ <url>@'"$URL"'@' /infrabox/upload/markup/checkmarx.json