#!/usr/bin/env bash

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a symlink
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    link=$(expr "$PRG" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=$(dirname "$PRG")"/$link"
    fi
done
SAVED="$(cd "$(dirname "$PRG")" >/dev/null 2>&1 && pwd)"
cd "$SAVED" >/dev/null 2>&1
APP_HOME=$(cd "$(dirname "$PRG")" >/dev/null 2>&1 && pwd)
cd "$ORIG_DIR" >/dev/null 2>&1

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here.
DEFAULT_JVM_OPTS=""

# Use the maximum available, or set MAX_FD != maximum.
MAX_FD="maximum"

warn ( ) {
    echo "$*"
}

die ( ) {
    echo
    echo "$*"
    echo
    exit 1
}

cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ] ; then
    die "ERROR: gradle-wrapper.jar not found in $APP_HOME/gradle/wrapper directory."
fi

# Now convert the arguments to Gradle-friendly format
# and collect values in $outClasspath
outClasspath=""
for arg in "$@" ; do
    case "$arg" in
        -*)
            ;;
        /)
            continue
            ;;
    esac
    outClasspath="$outClasspath$arg "
done
outClasspath=${outClasspath%% }

JAVA_HOME=$(/usr/libexec/java_home) 2>/dev/null
if [ $? -eq 0 ]; then
    exec "$JAVA_HOME/bin/java" -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
else
    exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
fi
