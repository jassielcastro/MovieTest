#!/bin/bash

helpFunction()
{
   echo ""
   if [ ${UPLOAD} = 1 ]
   then 
      echo "Usage: -u|--upload -s|--ssh [arg] -b|--branch [arg] -comment [args]"
      echo -e "\t-u|--upload action to upload changes"
      echo -e "\t-s|--ssh user private ssh key: to use DEFAUL, only set "default" as parameter"
      echo -e "\t-b|--branch name of remote Git Branch"
      echo -e "\t-comment Provide a commit message"
   elif [ ${PULL} = 1 ]
   then
      echo "Usage: -p|--pull -s|--ssh [arg] -b|--branch [arg]"
      echo -e "\t-p|--upload action to upload changes"
      echo -e "\t-s|--ssh user private ssh key: to use DEFAUL, only set "default" as parameter"
      echo -e "\t-b|--branch name of remote Git Branch to pull changes"
   else
      echo "Use an action: -p|--pull or -u|--upload"
   fi
   exit 1 # Exit script after printing help
}

uploadChanges()
{
   if ! [[ ${comment} = "" ]]
   then
         git add .
         git commit -m "${comment}"
      else
         echo "No commit message"
   fi

   git push origin ${branch}
}

pullChanges()
{
   git pull origin ${branch}  
}

ARGS=$(getopt -q "pus:b:" -l "pull,upload,ssh:,branch:" -n "Comments" -comment "$@");

if [ $? -ne 0 ];
then
    echo "Ha ocurrido un error al parsear los argumentos"
    helpFunction
    exit 1
fi

eval set -- "$ARGS";

UPLOAD=0
PULL=0

while [ $# -gt 0 ]; do
   case "$1" in
      -p|--pull)
         PULL=1
      ;;
      -u|--upload)
         UPLOAD=1
      ;;
      -s|--ssh)
         userKey="$2"
      ;;
      -b|--branch)
         branch="$2"
      ;;
      -comment)
         comment="$@"
         comment=${comment##*-comment }
      ;;
      ?)
         helpFunction
      ;;
   esac
  shift
done

if ! [ ${PULL} = 1 ] && ! [ ${UPLOAD} = 1 ]
then
   echo "Action not specified: -p|--pull or -u|--upload"
   exit 1
fi

# Print helpFunction in case parameters are empty
if [ -z "$userKey" ]
then
   echo "SSH cannot be empty";
   exit 1
fi

if [ -z "$branch" ]
then
   echo "Branch name cannot be empty";
   exit 1
fi

# default name of private ssh key 
defaultKey="gitHubAcer"

# validating user private ssh key name
if [[ ${userKey} == "default" ]]
then 
   # if the userKey is "default" then override the value for defaultKey
   userKey=${defaultKey}
   echo "Using DEFAULT as SSH private key"
else 
   echo "Using ${userKey} as SSH private key"
fi

eval "$(ssh-agent -s)"
ssh-add ~/.ssh/${userKey}

if [ ${PULL} = 1 ]
then 
   pullChanges
elif [ ${UPLOAD} = 1 ]
then
   uploadChanges
fi