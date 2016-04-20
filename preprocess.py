import os


class Node:
    def __init__(self, id_):
        self.id = id_
        self.desNodeIds = ""
        self.pagerank = 0.0

    def __str__(self):
        return "{};{};{};".format(self.id, self.desNodeIds, self.pagerank)


def preprocess(inputFilePath, outputFilePath):
    """
    Preprocess the input file, and output to specified path.
    Return True if it successes, otherwise False.

    The format of the input file is as follows:
    src_node_id dest_node_id random_floating_number

    The format of the output file is as follows: (tokens splitted by space)
    src_node_id dest_node1,dest_node2... src_node_PageRank
    """

    if not os.path.isfile(inputFilePath):
        print 'Input file' + inputFilePath + 'does not exist.'
        return False

    dic = {}      # node_id:str -> node:Node
    with open(inputFilePath, 'r') as f:
        for line in f:
            srcNodeId, desNodeId, nextPageRank = line.split()
            node = dic.get(srcNodeId, Node(srcNodeId))
            dic[srcNodeId] = node
            node.desNodeIds += desNodeId + ","
            node.pagerank += float(nextPageRank)

    with open(outputFilePath, 'w') as f:
        for node_id in sorted(dic.keys(), cmp=lambda x, y: int(x)-int(y)):
            f.write(str(dic[node_id]) + '\n')

    return True


if __name__ == '__main__':
    preprocess('./data/testcase1.txt', './data/simplepagerank_0')
    # preprocess('/Users/yhf/Documents/data/edges.txt',\
    #   '/Users/yhf/Documents/data/simplepagerank_0.txt')