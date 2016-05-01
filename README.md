# Overall Structure

### Simple PageRank

**PageRankMapper.java**  

```
keyIn:
valueIn: srcNodeId;desNodeId1,desNodeId...;
     
keyOut: srcNodeId
valueOut: NODEINFO;desNodeId1,desNodeId2...;srcOldNodePageRank;

foreach desNode:
	keyOut: desNodeId
	valueOut: NEXTPAGERANK;nextPageRank;
```

**PageRankReducer.java**

```
keyIn: srcNodeId
valueIn: NEXTPAGERANK;nextPageRank;
Or
valueIn: NODEINFO;desNodeId1,desNodeId2...;srcNodePageRank;

keyOut:
valueOut: srcNodeId;desNodeId1,desNodeId2...;srcNodeDegree;srcNodePageRank;
```

**PageRankRunner.java**
Set the input and output path from arguments, and run the hadoop jobs for 5 iterations.

**Counter.java**
Store the residual value in reducer in each iteraction.


### Block PageRank

**PageRankMapper.java**  

```
keyIn:
valueIn: uId;vId1, vId2, ...;pageRank;

KeyOut: blockId
valueOut: BE;vId;uId;NextPageRank
Or
KeyOut: blockId
valueOut: BC;vId;NextPageRank
Or
KeyOut: blockId
valueOut:NODEINFO;uId;vId1, vId2, ...;pageRank;
```

**PageRankReducer.java**
```
KeyIn: blockId
valueIn: BE;vId;uId;
Or
KeyIn: blockId
valueIn: BC;vId;NextPageRank
Or
KeyIn: blockId
valueIn: NODEINFO;uId;vId1, vId2, ...;pageRank;

KeyOut:
ValueOut: uId;vId1, vId2, ...;pageRank;
```

**PageRankRunner.java**
Set the input and output path from arguments, and run the hadoop jobs untill the residual error becomes less than 0.001.

**Counter.java**
Store the residual value in reducer and the number of inblock iteration in each iteraction.

**Node.java**
Store the information of the node.

### Gauss-Seidel PageRank
The java file functions are the same with the Block PageRank


# Usage

1. First, run the `preprocess.py` script to get a pre-filtered data set. In the main method, you need to specify input and output filepaths. It will generate a file named `simplepagerank_0`.

	The NetID used to filter out edges is `hy456`. Thus, `rejectMin = 0.654 * 0.9 = 0.5886`, `rejectLimit = rejectMin + 0.01 = 0.5986`. Edges actually selected in our graph is `7524427`.

2. Then, upload the pre-filterd file `simplepagerank_0` to AWS S3 bucket.

3. Compile the java source code and archive classes into jar file.

	```
	./deploy.sh SimplePageRank
	```
	
	```
	./deploy.sh BlockPageRank
	```
	
	```
	./deploy.sh GaussPageRank
	```

	Clean all the compiled files.
	
	```
	./clean.sh SimplePageRank
	```
	
	```
	./clean.sh BlockPageRank
	```
	
	```
	./clean.sh GaussPageRank
	```


# Results

### Simple PageRank:

```
Iteration 0 avg error 2.338487e+00
Iteration 1 avg error 3.230211e-01
Iteration 2 avg error 1.920783e-01
Iteration 3 avg error 9.410072e-02
Iteration 4 avg error 6.282157e-02
```

### Block PageRank:

```
```

### Gauss PageRank

```
```
