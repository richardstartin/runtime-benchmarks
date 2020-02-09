import os

import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats
import numpy as np


def plot_group(g, key1, key2):
    meta = g['classification']
    title = f'{meta[0]} histogram'
    series1 = g[key1]
    series2 = g[key2]
    summary = series1.join(series2, on='symbol', how='inner')
    summary['difference'] = summary[f'count_{key1}'] - summary[f'count_{key2}']
    summary['exists'] = summary[f'count_{key1}'] + summary[f'count_{key2}']
    ax = summary[summary['exists'] > 0].plot.bar(rot=315,
                                                 title=title,
                                                 y=[f'count_{key1}', f'count_{key2}', 'difference'])
    fig = ax.get_figure()
    filename = '_'.join(meta)
    fig.savefig(f'{filename}_histogram.png')


def plot_cdfs(g, key1, key2):
    meta = g['classification']
    title = f'{meta[0]} cumulative ranked frequency'
    series1 = g[key1][f'count_{key1}'].to_numpy()
    series2 = g[key2][f'count_{key2}'].to_numpy()
    cdf1 = np.divide(series1, series1.sum())
    cdf1 = cdf1.cumsum()
    cdf2 = np.divide(series2, series2.sum())
    cdf2 = cdf2.cumsum()
    fig, ax = plt.subplots()
    x = np.linspace(0, series1.size, series1.size)
    ax.plot(x, cdf1, label=key1)
    ax.plot(x, cdf2, label=key2)
    ax.set(xlabel='x', ylabel='cumulative ranked frequency', title=title)
    ax.set_ylim([0, 1.5])
    ax.set_xlim([x.min(), x.max()])
    ax.grid()
    ax.legend()
    filename = '_'.join(meta)
    fig.savefig(f'{filename}_cdf.png')


results_dir = '../results/'
groups = {}

for root, subdirs, files in os.walk(results_dir):
    for file in files:
        classification = root.replace(results_dir, '').split(os.sep)
        classification.append(file.replace('.csv', ''))
        classification.reverse()
        key = "_".join(classification[0:len(classification) - 1])
        if key not in groups:
            groups[key] = {'classification': classification[0:len(classification) - 1].copy()}
        group = groups[key]
        comparison = classification[len(classification) - 1]
        group[comparison] = pd.read_csv(f'{root}{os.sep}{file}', header=None, names=['symbol', f'count_{comparison}'],
                                        index_col='symbol')

for key in groups.keys():
    plot_group(groups[key], 'de', 'en')
    plot_cdfs(groups[key], 'de', 'en')
