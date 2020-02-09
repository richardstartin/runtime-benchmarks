import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from matplotlib import ticker
from matplotlib.colors import LinearSegmentedColormap

LANGUAGES = {
    'en': 'English',
    'de': 'German',
}


def plot_heatmap(df, key, value, filename):
    title = "%s adjacency matrix" % (LANGUAGES[key])
    series = pd.pivot_table(df, index='symbol1', columns='symbol2', values=[value], fill_value=0, aggfunc=np.sum)
    fig, ax = plt.subplots()
    total = df[value].sum()
    cmap = LinearSegmentedColormap.from_list('heat', ['lightgrey', 'red', 'darkorange', 'orange', 'gold', 'yellow'])
    im = ax.imshow(np.divide(series.values, total / 100), cmap=cmap, interpolation='hamming')
    ax.set_xticks(np.arange(series.columns.size))
    ax.set_xticklabels([str(chr(i)) for i in range(256)])
    ax.set_xlabel('second byte')
    ax.set_yticks(np.arange(series.index.size))
    ax.set_yticklabels([str(chr(i)) for i in range(256)])
    ax.set_ylabel('first byte')
    for n, label in enumerate(ax.xaxis.get_ticklabels()):
        label.set_visible(False)
    for n, label in enumerate(ax.yaxis.get_ticklabels()):
        label.set_visible(False)
    plt.setp(ax.get_xticklabels(), rotation=90, ha="right", rotation_mode="anchor")
    fig.colorbar(im, format=ticker.PercentFormatter())
    ax.set_title(title)
    fig.tight_layout()
    fig.savefig(f'{filename}_heatmap_{key}.png')
    return series


def plot_group(g, key1, key2):
    meta = g['classification']
    filename = '_'.join(meta)
    if meta[0] != 'pairs':
        title = f'{meta[0]} histogram'
        series1 = g[key1]
        series2 = g[key2]
        summary = series1.join(series2, on='symbol', how='inner')
        summary['difference'] = summary[f'count_{key1}'] - summary[f'count_{key2}']
        summary['exists'] = summary[f'count_{key1}'] + summary[f'count_{key2}']
        ax = summary[summary['exists'] > 0].plot.bar(title=title,
                                                     rot=0,
                                                     y=[f'count_{key1}', f'count_{key2}', 'difference'])
        ax.legend([LANGUAGES[key1], LANGUAGES[key2], 'Difference'])
        if meta[0] == 'base256':
            for n, label in enumerate(ax.xaxis.get_ticklabels()):
                label.set_visible(False)
        fig = ax.get_figure()
        fig.savefig(f'{filename}_histogram.png')
    else:
        plot_heatmap(g[key1], key1, f'count_{key1}', filename)
        plot_heatmap(g[key2], key2, f'count_{key2}', filename)


def plot_cdfs(g, key1, key2):
    meta = g['classification']
    if meta[0] != 'pairs':
        title = f'{meta[0]} cumulative ranked frequency'
        series1 = g[key1][f'count_{key1}'].to_numpy()
        series2 = g[key2][f'count_{key2}'].to_numpy()
        cdf1 = np.divide(series1, series1.sum())
        cdf1 = cdf1.cumsum()
        cdf2 = np.divide(series2, series2.sum())
        cdf2 = cdf2.cumsum()
        fig, ax = plt.subplots()
        x = np.linspace(0, series1.size, series1.size)
        ax.plot(x, cdf1, label=LANGUAGES[key1])
        ax.plot(x, cdf2, label=LANGUAGES[key2])
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
        base = file.replace('.csv', '')
        classification = root.replace(results_dir, '').split(os.sep)
        classification.append(base)
        classification.reverse()
        key = "_".join(classification[0:len(classification) - 1])
        if key not in groups:
            groups[key] = {'classification': classification[0:len(classification) - 1].copy()}
        group = groups[key]
        comparison = classification[len(classification) - 1]
        if base != 'pairs':
            group[comparison] = pd.read_csv(f'{root}{os.sep}{file}', header=None,
                                            names=['symbol', f'count_{comparison}'], index_col='symbol')
        else:
            group[comparison] = pd.read_csv(f'{root}{os.sep}{file}', header=None,
                                            names=['symbol1', 'symbol2', f'count_{comparison}'])
for key in groups.keys():
    plot_group(groups[key], 'de', 'en')
    plot_cdfs(groups[key], 'de', 'en')
