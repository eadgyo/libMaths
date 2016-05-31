name=$(basename target/*javadoc.jar)
group=${name%%-*}
idname=${name#*-}
idname=${idname%%-*}
version=${name#*-}
version=${version#*-}
version=${version%-*}

echo $name

echo $version

