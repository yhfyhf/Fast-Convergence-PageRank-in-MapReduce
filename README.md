# CS5300 Project 2

## Haonan Liu(hl955), Youdan Xu(yx339), Haofei Ying(hy456) 


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

1. First, run the `preprocess.py` script to get a pre-filtered data set. In the `main` method, you need to **specify input and output filepaths**. It will generate a file named `simplepagerank_0`.

	The NetID used to filter out edges is `hy456`. Thus, `rejectMin = 0.654 * 0.9 = 0.5886`, `rejectLimit = rejectMin + 0.01 = 0.5986`. Edges actually selected in our graph is `7524427`.

2. Then, upload the pre-filterd file `simplepagerank_0` to AWS S3 bucket under path `/data/`.

3. Compile the java source code and archive classes into jar file.

	```
	./deploy.sh [ SimplePageRank | BlockPageRank | GaussPageRank | RandomPageRank ]
	```

	Clean all the compiled files and delete the existing input and output paths on S3.
	
	```
	./clean.sh [ SimplePageRank | BlockPageRank | GaussPageRank | RandomPageRank ]
	```
	
	**NOTE:**  You need to specify your `bucket_name` and `cluster_id` in `deploy.sh` before executing it. Also, you need to execute `clean.sh` to avoid the `FileAlreadyExistsException`.


# Results

### Simple PageRank:

```
Average residual errors
Iteration 0 avg error 2.338487e+00
Iteration 1 avg error 3.230211e-01
Iteration 2 avg error 1.920783e-01
Iteration 3 avg error 9.410072e-02
Iteration 4 avg error 6.282157e-02
```

### Block PageRank:

```
Average residual errors
Iteration 0 avg error 2.815284e+00
Iteration 1 avg error 3.809232e-02
Iteration 2 avg error 2.370591e-02
Iteration 3 avg error 9.744174e-03
Iteration 4 avg error 3.806021e-03
Iteration 5 avg error 9.442085e-04

The average number of iterations per Mapreduce per node
Iteration 0 inblock iter 17
Iteration 1 inblock iter 7
Iteration 2 inblock iter 6
Iteration 3 inblock iter 4
Iteration 4 inblock iter 3
Iteration 5 inblock iter 1

The average number of iterations per Block
block0 iter num: 7
block1 iter num: 7
block2 iter num: 12
block3 iter num: 6
block4 iter num: 8
block5 iter num: 9
block6 iter num: 7
block7 iter num: 6
block8 iter num: 8
block9 iter num: 7
block10 iter num: 11
block11 iter num: 8
block12 iter num: 5
block13 iter num: 7
block14 iter num: 3
block15 iter num: 3
block16 iter num: 3
block17 iter num: 7
block18 iter num: 7
block19 iter num: 9
block20 iter num: 12
block21 iter num: 6
block22 iter num: 7
block23 iter num: 5
block24 iter num: 6
block25 iter num: 7
block26 iter num: 8
block27 iter num: 5
block28 iter num: 10
block29 iter num: 13
block30 iter num: 13
block31 iter num: 13
block32 iter num: 18
block33 iter num: 10
block34 iter num: 9
block35 iter num: 8
block36 iter num: 7
block37 iter num: 9
block38 iter num: 7
block39 iter num: 5
block40 iter num: 6
block41 iter num: 7
block42 iter num: 9
block43 iter num: 8
block44 iter num: 4
block45 iter num: 5
block46 iter num: 7
block47 iter num: 7
block48 iter num: 7
block49 iter num: 13
block50 iter num: 6
block51 iter num: 5
block52 iter num: 11
block53 iter num: 7
block54 iter num: 5
block55 iter num: 4
block56 iter num: 4
block57 iter num: 8
block58 iter num: 5
block59 iter num: 4
block60 iter num: 6
block61 iter num: 6
block62 iter num: 6
block63 iter num: 5
block64 iter num: 5
block65 iter num: 3
block66 iter num: 6
block67 iter num: 5


The PageRank values for the two lowest-numbered Nodes in each Block
blockId:0  nodeId:0, pr:1.7662844E-5
blockId:0  nodeId:1, pr:5.3695385E-5
blockId:1  nodeId:10328, pr:4.1881407E-7
blockId:1  nodeId:10329, pr:2.4268616E-7
blockId:10  nodeId:100501, pr:7.6893843E-7
blockId:10  nodeId:100502, pr:1.1493624E-6
blockId:11  nodeId:110567, pr:0.0010613861
blockId:11  nodeId:110568, pr:9.436223E-5
blockId:12  nodeId:120945, pr:8.120434E-7
blockId:12  nodeId:120946, pr:6.764715E-7
blockId:13  nodeId:130999, pr:2.2901064E-7
blockId:13  nodeId:131000, pr:2.2901064E-7
blockId:14  nodeId:140574, pr:0.0010480268
blockId:14  nodeId:140575, pr:2.2872065E-7
blockId:15  nodeId:150953, pr:9.982691E-4
blockId:15  nodeId:150954, pr:0.0018048264
blockId:16  nodeId:161332, pr:2.2856108E-7
blockId:16  nodeId:161333, pr:2.2856108E-7
blockId:17  nodeId:171154, pr:2.2938102E-7
blockId:17  nodeId:171155, pr:2.2938102E-7
blockId:18  nodeId:181514, pr:2.2446451E-7
blockId:18  nodeId:181515, pr:7.4294576E-4
blockId:19  nodeId:191625, pr:2.593737E-4
blockId:19  nodeId:191626, pr:2.1890456E-7
blockId:2  nodeId:20373, pr:2.1890456E-7
blockId:2  nodeId:20374, pr:2.1890456E-7
blockId:20  nodeId:202004, pr:9.3714283E-7
blockId:20  nodeId:202005, pr:9.931775E-7
blockId:21  nodeId:212383, pr:0.0011738206
blockId:21  nodeId:212384, pr:2.1890456E-7
blockId:22  nodeId:222762, pr:2.2839134E-7
blockId:22  nodeId:222763, pr:2.2839134E-7
blockId:23  nodeId:232593, pr:2.3665325E-7
blockId:23  nodeId:232594, pr:2.9685523E-7
blockId:24  nodeId:242878, pr:1.0812148E-6
blockId:24  nodeId:242879, pr:5.8003235E-7
blockId:25  nodeId:252938, pr:6.115055E-6
blockId:25  nodeId:252939, pr:5.7966827E-7
blockId:26  nodeId:263149, pr:9.773459E-6
blockId:26  nodeId:263150, pr:1.7914247E-6
blockId:27  nodeId:273210, pr:2.7178543E-5
blockId:27  nodeId:273211, pr:8.344093E-7
blockId:28  nodeId:283473, pr:2.8673708E-7
blockId:28  nodeId:283474, pr:2.8673708E-7
blockId:29  nodeId:293255, pr:2.9186053E-6
blockId:29  nodeId:293256, pr:7.0410792E-6
blockId:3  nodeId:30629, pr:2.653755E-7
blockId:3  nodeId:30630, pr:2.1890456E-7
blockId:30  nodeId:303043, pr:9.99592E-7
blockId:30  nodeId:303044, pr:5.4063967E-6
blockId:31  nodeId:313370, pr:2.549086E-7
blockId:31  nodeId:313371, pr:2.3471235E-5
blockId:32  nodeId:323522, pr:5.5895447E-7
blockId:32  nodeId:323523, pr:1.3505381E-6
blockId:33  nodeId:333883, pr:1.8997793E-5
blockId:33  nodeId:333884, pr:4.294905E-6
blockId:34  nodeId:343663, pr:2.12809E-6
blockId:34  nodeId:343664, pr:3.6849823E-7
blockId:35  nodeId:353645, pr:1.974418E-6
blockId:35  nodeId:353646, pr:9.506203E-7
blockId:36  nodeId:363929, pr:1.4328717E-5
blockId:36  nodeId:363930, pr:4.5782957E-7
blockId:37  nodeId:374236, pr:2.3927763E-7
blockId:37  nodeId:374237, pr:1.02101285E-5
blockId:38  nodeId:384554, pr:6.639836E-5
blockId:38  nodeId:384555, pr:5.353761E-6
blockId:39  nodeId:394929, pr:3.9610222E-6
blockId:39  nodeId:394930, pr:2.6018472E-6
blockId:4  nodeId:40645, pr:2.4612109E-5
blockId:4  nodeId:40646, pr:2.4612109E-5
blockId:40  nodeId:404712, pr:7.101309E-7
blockId:40  nodeId:404713, pr:8.2066214E-7
blockId:41  nodeId:414617, pr:3.7004725E-7
blockId:41  nodeId:414618, pr:3.7004725E-7
blockId:42  nodeId:424747, pr:1.0904894E-6
blockId:42  nodeId:424748, pr:3.633363E-6
blockId:43  nodeId:434707, pr:5.1756234E-7
blockId:43  nodeId:434708, pr:3.1663199E-6
blockId:44  nodeId:444489, pr:4.922495E-7
blockId:44  nodeId:444490, pr:2.396862E-7
blockId:45  nodeId:454285, pr:3.502824E-7
blockId:45  nodeId:454286, pr:3.1814386E-7
blockId:46  nodeId:464398, pr:5.291867E-7
blockId:46  nodeId:464399, pr:5.291867E-7
blockId:47  nodeId:474196, pr:5.194032E-7
blockId:47  nodeId:474197, pr:8.366151E-7
blockId:48  nodeId:484050, pr:1.6480262E-5
blockId:48  nodeId:484051, pr:1.2461775E-5
blockId:49  nodeId:493968, pr:8.240514E-7
blockId:49  nodeId:493969, pr:1.3858079E-6
blockId:5  nodeId:50462, pr:0.0049371514
blockId:5  nodeId:50463, pr:0.005076542
blockId:50  nodeId:503752, pr:7.909762E-7
blockId:50  nodeId:503753, pr:7.909762E-7
blockId:51  nodeId:514131, pr:0.0011306099
blockId:51  nodeId:514132, pr:2.6927848E-5
blockId:52  nodeId:524510, pr:8.431597E-4
blockId:52  nodeId:524511, pr:1.3700465E-5
blockId:53  nodeId:534709, pr:0.009103795
blockId:53  nodeId:534710, pr:4.1387407E-6
blockId:54  nodeId:545088, pr:0.001760785
blockId:54  nodeId:545089, pr:3.3292647E-5
blockId:55  nodeId:555467, pr:0.0017870528
blockId:55  nodeId:555468, pr:7.783612E-7
blockId:56  nodeId:565846, pr:2.1890456E-7
blockId:56  nodeId:565847, pr:2.1890456E-7
blockId:57  nodeId:576225, pr:1.18927155E-5
blockId:57  nodeId:576226, pr:2.1890456E-7
blockId:58  nodeId:586604, pr:3.8985116E-4
blockId:58  nodeId:586605, pr:2.989422E-7
blockId:59  nodeId:596585, pr:1.3329229E-6
blockId:59  nodeId:596586, pr:1.3862228E-6
blockId:6  nodeId:60841, pr:1.1812559E-5
blockId:6  nodeId:60842, pr:3.0127403E-6
blockId:60  nodeId:606367, pr:3.1642938E-7
blockId:60  nodeId:606368, pr:2.249009E-7
blockId:61  nodeId:616148, pr:7.955278E-7
blockId:61  nodeId:616149, pr:4.2555445E-7
blockId:62  nodeId:626448, pr:2.1890456E-7
blockId:62  nodeId:626449, pr:2.583953E-7
blockId:63  nodeId:636240, pr:2.2849078E-7
blockId:63  nodeId:636241, pr:2.2901064E-7
blockId:64  nodeId:646022, pr:5.682022E-7
blockId:64  nodeId:646023, pr:4.1093793E-7
blockId:65  nodeId:655804, pr:2.2849078E-7
blockId:65  nodeId:655805, pr:2.1890456E-7
blockId:66  nodeId:665666, pr:2.5611834E-7
blockId:66  nodeId:665667, pr:4.024848E-7
blockId:67  nodeId:675448, pr:2.1890456E-7
blockId:67  nodeId:675449, pr:4.9050273E-6
blockId:7  nodeId:70591, pr:2.4612109E-5
blockId:7  nodeId:70592, pr:2.4612109E-5
blockId:8  nodeId:80118, pr:5.3006863E-7
blockId:8  nodeId:80119, pr:9.80627E-7
blockId:9  nodeId:90497, pr:9.155742E-7
blockId:9  nodeId:90498, pr:1.1305711E-5
```

### Gauss PageRank

```
Average residual errors
Iteration 0 avg error 5.553232e+00
Iteration 1 avg error 2.642470e-02
Iteration 2 avg error 7.954993e-03
Iteration 3 avg error 3.118661e-03
Iteration 4 avg error 1.259431e-03
Iteration 5 avg error 3.867315e-04

The average number of iterations per Mapreduce per node
Iteration 0 inblock iter 2
Iteration 1 inblock iter 2
Iteration 2 inblock iter 2
Iteration 3 inblock iter 2
Iteration 4 inblock iter 1
Iteration 5 inblock iter 1

The average number of iterations per Block
block0 iter num: 2
block1 iter num: 1
block2 iter num: 2
block3 iter num: 1
block4 iter num: 2
block5 iter num: 1
block6 iter num: 2
block7 iter num: 2
block8 iter num: 2
block9 iter num: 2
block10 iter num: 2
block11 iter num: 2
block12 iter num: 2
block13 iter num: 2
block14 iter num: 1
block15 iter num: 1
block16 iter num: 1
block17 iter num: 2
block18 iter num: 2
block19 iter num: 2
block20 iter num: 2
block21 iter num: 2
block22 iter num: 2
block23 iter num: 1
block24 iter num: 1
block25 iter num: 2
block26 iter num: 2
block27 iter num: 2
block28 iter num: 2
block29 iter num: 2
block30 iter num: 2
block31 iter num: 2
block32 iter num: 2
block33 iter num: 2
block34 iter num: 1
block35 iter num: 1
block36 iter num: 1
block37 iter num: 1
block38 iter num: 1
block39 iter num: 1
block40 iter num: 1
block41 iter num: 2
block42 iter num: 2
block43 iter num: 2
block44 iter num: 1
block45 iter num: 1
block46 iter num: 2
block47 iter num: 2
block48 iter num: 2
block49 iter num: 2
block50 iter num: 2
block51 iter num: 1
block52 iter num: 2
block53 iter num: 2
block54 iter num: 1
block55 iter num: 2
block56 iter num: 1
block57 iter num: 2
block58 iter num: 1
block59 iter num: 2
block60 iter num: 2
block61 iter num: 2
block62 iter num: 1
block63 iter num: 1
block64 iter num: 1
block65 iter num: 1
block66 iter num: 2
block67 iter num: 1


The PageRank values for the two lowest-numbered Nodes in each Block
blockId:0  nodeId:0, pr:2.6917796E-6
blockId:0  nodeId:1, pr:3.3014796E-6
blockId:1  nodeId:10328, pr:2.955301E-7
blockId:1  nodeId:10329, pr:2.1890456E-7
blockId:10  nodeId:100501, pr:2.1890456E-7
blockId:10  nodeId:100502, pr:2.1890456E-7
blockId:11  nodeId:110567, pr:1.21030804E-4
blockId:11  nodeId:110568, pr:2.1890456E-7
blockId:12  nodeId:120945, pr:2.1890456E-7
blockId:12  nodeId:120946, pr:2.1890456E-7
blockId:13  nodeId:130999, pr:2.2454302E-7
blockId:13  nodeId:131000, pr:2.2454302E-7
blockId:14  nodeId:140574, pr:5.420391E-4
blockId:14  nodeId:140575, pr:2.2437717E-7
blockId:15  nodeId:150953, pr:5.1592133E-4
blockId:15  nodeId:150954, pr:7.453649E-4
blockId:16  nodeId:161332, pr:2.1890456E-7
blockId:16  nodeId:161333, pr:2.1890456E-7
blockId:17  nodeId:171154, pr:2.1890456E-7
blockId:17  nodeId:171155, pr:2.1890456E-7
blockId:18  nodeId:181514, pr:2.2344283E-7
blockId:18  nodeId:181515, pr:2.1890456E-7
blockId:19  nodeId:191625, pr:8.663219E-6
blockId:19  nodeId:191626, pr:2.1890456E-7
blockId:2  nodeId:20373, pr:2.1890456E-7
blockId:2  nodeId:20374, pr:2.1890456E-7
blockId:20  nodeId:202004, pr:2.1890456E-7
blockId:20  nodeId:202005, pr:2.1890456E-7
blockId:21  nodeId:212383, pr:2.0140444E-5
blockId:21  nodeId:212384, pr:2.1890456E-7
blockId:22  nodeId:222762, pr:2.1890456E-7
blockId:22  nodeId:222763, pr:2.1890456E-7
blockId:23  nodeId:232593, pr:2.1890456E-7
blockId:23  nodeId:232594, pr:2.1890456E-7
blockId:24  nodeId:242878, pr:3.1470552E-7
blockId:24  nodeId:242879, pr:2.1890456E-7
blockId:25  nodeId:252938, pr:2.1890456E-7
blockId:25  nodeId:252939, pr:2.1890456E-7
blockId:26  nodeId:263149, pr:2.6215673E-6
blockId:26  nodeId:263150, pr:2.1890456E-7
blockId:27  nodeId:273210, pr:2.1890456E-7
blockId:27  nodeId:273211, pr:2.1890456E-7
blockId:28  nodeId:283473, pr:2.1890456E-7
blockId:28  nodeId:283474, pr:2.1890456E-7
blockId:29  nodeId:293255, pr:2.3797355E-7
blockId:29  nodeId:293256, pr:2.1890456E-7
blockId:3  nodeId:30629, pr:2.2634731E-7
blockId:3  nodeId:30630, pr:2.1890456E-7
blockId:30  nodeId:303043, pr:2.2667437E-7
blockId:30  nodeId:303044, pr:9.3640386E-7
blockId:31  nodeId:313370, pr:2.4991604E-7
blockId:31  nodeId:313371, pr:2.4492435E-6
blockId:32  nodeId:323522, pr:2.3581991E-7
blockId:32  nodeId:323523, pr:2.8908522E-7
blockId:33  nodeId:333883, pr:1.2511396E-6
blockId:33  nodeId:333884, pr:2.1890456E-7
blockId:34  nodeId:343663, pr:3.8330973E-7
blockId:34  nodeId:343664, pr:2.680799E-7
blockId:35  nodeId:353645, pr:3.4110846E-7
blockId:35  nodeId:353646, pr:2.1890456E-7
blockId:36  nodeId:363929, pr:7.4048086E-7
blockId:36  nodeId:363930, pr:2.5743992E-7
blockId:37  nodeId:374236, pr:2.3751144E-7
blockId:37  nodeId:374237, pr:2.1890456E-7
blockId:38  nodeId:384554, pr:2.730834E-6
blockId:38  nodeId:384555, pr:2.1890456E-7
blockId:39  nodeId:394929, pr:2.3921274E-7
blockId:39  nodeId:394930, pr:2.6245544E-7
blockId:4  nodeId:40645, pr:8.711444E-6
blockId:4  nodeId:40646, pr:8.711444E-6
blockId:40  nodeId:404712, pr:2.809275E-7
blockId:40  nodeId:404713, pr:2.1890456E-7
blockId:41  nodeId:414617, pr:2.1890456E-7
blockId:41  nodeId:414618, pr:2.1890456E-7
blockId:42  nodeId:424747, pr:2.1890456E-7
blockId:42  nodeId:424748, pr:2.1890456E-7
blockId:43  nodeId:434707, pr:2.1890456E-7
blockId:43  nodeId:434708, pr:2.8137038E-7
blockId:44  nodeId:444489, pr:2.5609503E-7
blockId:44  nodeId:444490, pr:2.1890456E-7
blockId:45  nodeId:454285, pr:2.664664E-7
blockId:45  nodeId:454286, pr:2.1890456E-7
blockId:46  nodeId:464398, pr:2.9664838E-7
blockId:46  nodeId:464399, pr:2.9664838E-7
blockId:47  nodeId:474196, pr:2.8486392E-7
blockId:47  nodeId:474197, pr:2.1890456E-7
blockId:48  nodeId:484050, pr:1.974814E-6
blockId:48  nodeId:484051, pr:2.1890456E-7
blockId:49  nodeId:493968, pr:2.3772532E-7
blockId:49  nodeId:493969, pr:4.3155472E-7
blockId:5  nodeId:50462, pr:0.0017778346
blockId:5  nodeId:50463, pr:0.001789626
blockId:50  nodeId:503752, pr:2.1890456E-7
blockId:50  nodeId:503753, pr:2.1890456E-7
blockId:51  nodeId:514131, pr:1.7408875E-4
blockId:51  nodeId:514132, pr:2.1890456E-7
blockId:52  nodeId:524510, pr:2.383244E-5
blockId:52  nodeId:524511, pr:2.1890456E-7
blockId:53  nodeId:534709, pr:0.0014140059
blockId:53  nodeId:534710, pr:9.058025E-7
blockId:54  nodeId:545088, pr:1.5198407E-4
blockId:54  nodeId:545089, pr:2.1890456E-7
blockId:55  nodeId:555467, pr:2.4756152E-4
blockId:55  nodeId:555468, pr:2.462755E-7
blockId:56  nodeId:565846, pr:2.1890456E-7
blockId:56  nodeId:565847, pr:2.1890456E-7
blockId:57  nodeId:576225, pr:2.3785208E-6
blockId:57  nodeId:576226, pr:2.1890456E-7
blockId:58  nodeId:586604, pr:3.5366572E-6
blockId:58  nodeId:586605, pr:2.2076524E-7
blockId:59  nodeId:596585, pr:2.1890456E-7
blockId:59  nodeId:596586, pr:2.1890456E-7
blockId:6  nodeId:60841, pr:8.4031893E-7
blockId:6  nodeId:60842, pr:2.1890456E-7
blockId:60  nodeId:606367, pr:2.552269E-7
blockId:60  nodeId:606368, pr:2.1890456E-7
blockId:61  nodeId:616148, pr:2.5062084E-7
blockId:61  nodeId:616149, pr:2.1890456E-7
blockId:62  nodeId:626448, pr:2.1890456E-7
blockId:62  nodeId:626449, pr:2.1890456E-7
blockId:63  nodeId:636240, pr:2.2422081E-7
blockId:63  nodeId:636241, pr:2.2454302E-7
blockId:64  nodeId:646022, pr:2.1890456E-7
blockId:64  nodeId:646023, pr:2.1890456E-7
blockId:65  nodeId:655804, pr:2.2422081E-7
blockId:65  nodeId:655805, pr:2.1890456E-7
blockId:66  nodeId:665666, pr:2.5611834E-7
blockId:66  nodeId:665667, pr:2.2323174E-7
blockId:67  nodeId:675448, pr:2.1890456E-7
blockId:67  nodeId:675449, pr:2.1890456E-7
blockId:7  nodeId:70591, pr:8.711444E-6
blockId:7  nodeId:70592, pr:8.711444E-6
blockId:8  nodeId:80118, pr:2.1890456E-7
blockId:8  nodeId:80119, pr:2.1890456E-7
blockId:9  nodeId:90497, pr:2.1890456E-7
blockId:9  nodeId:90498, pr:9.941915E-7
```
