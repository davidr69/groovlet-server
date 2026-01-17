package net.lavacro.serverless.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GitTreeResponse {
	private String sha;
	private String url;
	private List<GitTreeItem> tree;
	private Boolean truncated;
}

/*
{
    "sha": "806d994505ea7b73a60c7120fabcd6d8615e4c43",
    "url": "https://api.github.com/repos/davidr69/api_access/git/trees/806d994505ea7b73a60c7120fabcd6d8615e4c43",
    "tree": [
        {
            "path": ".gitignore",
            "mode": "100644",
            "type": "blob",
            "sha": "bb69167950e2d72b729196db047b2aed79a9d74b",
            "size": 67,
            "url": "https://api.github.com/repos/davidr69/api_access/git/blobs/bb69167950e2d72b729196db047b2aed79a9d74b"
        },
        {
            "path": "app1",
            "mode": "040000",
            "type": "tree",
            "sha": "8f307353273db64afc38f81e96a52bef76333936",
            "url": "https://api.github.com/repos/davidr69/api_access/git/trees/8f307353273db64afc38f81e96a52bef76333936"
        },
        {
            "path": "app2",
            "mode": "040000",
            "type": "tree",
            "sha": "1356808512696c085bc8145d4f3725535d60ae85",
            "url": "https://api.github.com/repos/davidr69/api_access/git/trees/1356808512696c085bc8145d4f3725535d60ae85"
        }
    ],
    "truncated": false
}
 */