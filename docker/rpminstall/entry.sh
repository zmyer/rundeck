#!/bin/bash
. /rd-util.sh

if ! find "$HOME/rundeck/packaging/rpmdist/RPMS/noarch/" -name '*.rpm' ; then
	echo "rpm not found at $HOME/rundeck/packaging/rpmdist/RPMS/noarch/rundeck*.rpm"
	exit 2
fi

rpm -ivh "$HOME"/rundeck/packaging/rpmdist/RPMS/noarch/rundeck*.rpm

entry_start "$*"
