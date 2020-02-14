import pandas as pd


def rename_columns(name):
    if name == 'Score Error (99.9%)':
        return 'error'
    else:
        return str.lower(name).replace('param: ', '')


file = '../searcher.csv'
df = pd.read_csv(file)
df = df.rename(rename_columns, axis='columns')
df = df.drop(['benchmark', 'mode', 'threads', 'samples', 'seed', 'unit'], axis=1)
df.set_index('searchertype')
df = df.loc[(df['searchertype'] == 'UNSAFE_SPARSE_BIT_MATRIX_SWAR')
            & (df['logvariety'] == 12)
            & (df['datalength'] == 2000)]\
    .drop(['searchertype', 'logvariety', 'datalength'], axis=1)

df = df.pivot(index='termlength', columns='dataset')
row = df['score'].max()
upper = -1
for i in range(len(row)):
    upper = max(row[i], upper)
ax = df.plot.bar(y='score', yerr='error', title='UNSAFE_SPARSE_BIT_MATRIX_SWAR (μs/op)')
ax.set_ylim([0, 2 * upper])
ax.get_figure().savefig('benchmark.png')

