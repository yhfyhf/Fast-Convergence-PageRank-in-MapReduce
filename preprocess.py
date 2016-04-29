"""
Filter out 1% edges, and preprocess the file.

Usage: You need to specify these arguments in the main method:

    @input filepath
    @intermediate filepath of filtered edges
    @output filepath
"""


def preprocess(input_filepath, filtered_filepath, output_filepath):
    """
    Preprocesses the input file.
    Selects 99% lines into filtered_filepath, and reformats into output_filepath.
    """
    reject_random_lines(input_filepath, filtered_filepath)
    reformat(filtered_filepath, output_filepath)


def reject_random_lines(input_filepath, output_filepath):
    """
    Rejects around 1% lines from the input file, filtered by the trailing
    random floating number in each lines.

    The lower bound and upper bound of the filter band are decided by a NetID.
    The NetID selected in this case is hy456.
    """
    from_netid = 0.654                  # 456 is 654 reversed
    reject_min = 0.9 * from_netid
    reject_limit = reject_min + 0.01

    with open(output_filepath, 'w') as output_f:
        with open(input_filepath, 'r') as input_f:
            map(output_f.write, filter(lambda line: float(line.split()[2]) < reject_min or float(line.split()[2]) >= reject_limit, input_f))


def reformat(input_filepath, output_filepath):
    """
    Reformats the input file, and output to specified path.
    Returns True if it successes, otherwise False.

    The format of the input file is as follows:
    src_node_id dest_node_id random_floating_number

    The format of the output file is as follows: (tokens splitted by ';')
    src_node_id;dest_node1,dest_node2,dest_node3;1/685230
    """
    with open(output_filepath, 'w') as output_f:

        prev_src_node_id = "-1"
        dest_node_ids = ""
        init_pagerank = '{:.25f}'.format(1.0 / 685230)

        with open(input_filepath, 'r') as input_f:
            for line in input_f:
                src_node_id, dest_node_id, random_num = line.split()
                if prev_src_node_id != src_node_id:
                    if prev_src_node_id != "-1":
                        output_f.write("{};{};{}\n".format(prev_src_node_id, dest_node_ids, init_pagerank))
                    for node_id in xrange(int(prev_src_node_id)+1, int(src_node_id)):   # isolation nodes
                        output_f.write("{};{};{}\n".format(node_id, "", init_pagerank))
                    prev_src_node_id = src_node_id
                    dest_node_ids = dest_node_id
                else:
                    dest_node_ids += "," + dest_node_id

        for node_id in xrange(int(prev_src_node_id)+1, int(src_node_id)):   # isolation nodes
            output_f.write("{};{};{}\n".format(node_id, "", init_pagerank))
        output_f.write("{};{};{}\n".format(src_node_id, dest_node_ids, init_pagerank))


if __name__ == '__main__':
    # reformat('./data/edges.txt', './data/simplepagerank_0')
    preprocess('/Users/yhf/Documents/data/edges.txt', '/Users/yhf/Documents/data/filtered_edges.txt', '/Users/yhf/Documents/data/simplepagerank_0')
    # preprocess('./data/edges.txt', './data/filtered_edges.txt', './data/simplepagerank_0')
