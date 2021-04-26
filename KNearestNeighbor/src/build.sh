#Currently not working as i have to compile multiple source files and im not sure how to do that robustly, so im compiling manually.
if [ $# -ne 2 ]
then
    printf "usage: build.sh [run prog || compile prog.java]\n"
    exit 1    
fi


if [ $1 = "compile" ]
then
    javac -cp ".:../lib/*" $2   
    exit 0
fi

if [ $1 = "run" ]
then
    java -cp ".:../lib/*" $2
    exit 0
fi
printf "usage: build.sh [run prog || compile prog.java]\n"
exit 1
