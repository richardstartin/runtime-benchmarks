import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from matplotlib import ticker
from matplotlib.colors import LinearSegmentedColormap

LANGUAGES = {
    'en': 'English',
    'de': 'German',
    'zh': 'Chinese (Traditional)',
    'sh': 'Serbian (Latin script)',
    'ru': 'Russian'
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


def plot_group(g, keys, suffix):
    meta = g['classification']
    filename = '_'.join(meta)
    if meta[0] != 'pairs' and meta[0] != 'base64pairs':
        title = f'{meta[0]} histogram'
        summary = None
        counts = []
        legend = []
        for k in keys:
            series = g[k]
            if summary is None:
                summary = series
            else:
                summary = summary.join(series.set_index('symbol'), on='symbol', how='inner')
            counts.append(f'count_{k}')
            legend.append(LANGUAGES[k])
        ax = summary.plot.bar(title=title,
                              rot=0,
                              x='symbol',
                              y=counts)
        ax.legend(legend)
        if meta[0] == 'base256':
            for n, label in enumerate(ax.xaxis.get_ticklabels()):
                label.set_visible(False)
        fig = ax.get_figure()
        fig.savefig(f'{filename}_histogram_{suffix}.png')
    else:
        for k in keys:
            plot_heatmap(g[k], k, f'count_{k}', filename)


def plot_cdfs(g, keys):
    meta = g['classification']
    if meta[0] != 'pairs':
        title = f'{meta[0]} cumulative ranked frequency'
        cdfs = {}
        widest = -1
        for key in keys:
            series = g[key][f'count_{key}'].to_numpy()
            cdf = np.divide(series, series.sum())
            cdf = cdf.cumsum()
            cdfs[key] = cdf
            widest = max(widest, series.size)
        fig, ax = plt.subplots()
        x = np.linspace(0, widest, widest)
        for key in cdfs.keys():
            ax.plot(x, cdfs[key], label=LANGUAGES[key])
        ax.set(xlabel='x', ylabel='cumulative ranked frequency', title=title)
        ax.set_ylim([0, 1.5])
        ax.set_xlim([x.min(), x.max()])
        ax.grid()
        ax.legend()
        filename = '_'.join(meta)
        fig.savefig(f'{filename}_cdf.png')


def plot_language(group, key):
    meta = group['classification']
    if meta[0] != 'pairs':
        filename = '_'.join(meta)
        title = f'{LANGUAGES[key]} {meta[0]} ranked'
        series = group[key].sort_values(by=f'count_{key}', ascending=False)
        series['pct'] = series[f'count_{key}'].div(group[key][f'count_{key}'].sum() / 100)

        def to_char(input):
            if isinstance(input, int):
                return str(chr(input))
            else:
                return input

        series['character'] = series['symbol'].apply(to_char)
        ax = series[series[f'count_{key}'] > 0].plot.bar(title=title, rot=0, x='character', y='pct')
        ax.legend(['Frequency'])
        ax.yaxis.set_major_formatter(ticker.PercentFormatter())
        if meta[0] == 'base256':
            for n, label in enumerate(ax.xaxis.get_ticklabels()):
                if n > 33 or n % 4 != 1:
                    label.set_visible(False)
        fig = ax.get_figure()
        fig.savefig(f'{filename}_{key}_ranked.png')


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
        if base != 'pairs' and base != 'base64pairs':
            group[comparison] = pd.read_csv(f'{root}{os.sep}{file}', header=None,
                                            names=['symbol', f'count_{comparison}'])
        else:
            group[comparison] = pd.read_csv(f'{root}{os.sep}{file}', header=None,
                                            names=['symbol1', 'symbol2', f'count_{comparison}'])
for key in groups.keys():
    plot_group(groups[key], ['de', 'en'], 'germanic')
    plot_group(groups[key], ['zh', 'ru', 'sh'], 'others')
    plot_cdfs(groups[key], ['de', 'en'])

