#!/bin/zsh

git push origin master;
NEWVERSION=$( git ls-remote --tags -q | cut -f 2 | cut -d \/ -f 3 | sort -t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr | head -n 1 | perl -pe 's/^((\d+\.)*)(\d+)(.*)$/$1.($3+1).$4/e');
git tag $NEWVERSION;
git push origin --tags;