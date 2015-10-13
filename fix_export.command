DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
APP="$(echo "${DIR##*/}")"
cp $DIR/images/ $DIR/$APP.app
if cp $DIR/images/* $DIR/application.macosx/$APP.app/
then echo "Copy successful. Attempt to open the application."
else
echo "Copy failed. Check that fix_export.command is located in your sketch folder and try again."
fi

