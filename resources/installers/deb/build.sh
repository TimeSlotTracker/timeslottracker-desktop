#!/bin/sh
export release=$1

sed -e "s/\${release}/${release}/" ./changelog > ./debian/changelog

fakeroot ./debian/rules clean binary-arch
mv ../timeslottracker_${release}_all.deb ../../../target/
