bucket_name=cs5300-project2-hy456 


if [ "$1" != "SimplePageRank" ] && [ "$1" != "BlockPageRank" ] && [ "$1" != "GaussPageRank" ]; then
	echo "Incorrect argument."
	exit 1
fi

rm -rf "out/$1Out"
for i in $(seq 1 10); do
    aws s3 rm --recursive s3://$bucket_name/data/simplepagerank_$i
done
