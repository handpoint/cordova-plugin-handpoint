#!/bin/zsh

git push origin master;
NEWVERSION=$(git ls-remote --tags -q | tail -n 1 -r | cut -f 2 | cut -d \/ -f 3 | perl -pe 's/^((\d+\.)*)(\d+)(.*)$/$1.($3+1).$4/e');
git tag $NEWVERSION;
git push origin --tags;