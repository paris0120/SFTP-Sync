package com.parisliu.SFTP_Sync;

import java.awt.Color;
import java.awt.Font;

import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ScrollPane;
import org.apache.pivot.wtk.TreeView;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.TreeBranch;
import org.apache.pivot.wtk.content.TreeNode;

public class GUI implements Application {
    private Window window = null;

    @Override
    public void startup(Display display, Map<String, String> properties) {
        window = new Window();
        
        TreeBranch tree = new TreeBranch();
        tree.setText("test tree");
        
        TreeNode treeNode = new TreeNode();
        treeNode.setText("FD");
       
        tree.add(treeNode);
        TreeView t = new TreeView();
        Border b = new Border();
        ScrollPane s = new ScrollPane();
        b.add(s);
        s.add(t);
        t.load(tree);
        
        
        Label label = new Label();
        label.setText("Hello World!");
        label.getStyles().put("font", new Font("Arial", Font.BOLD, 24));
        label.getStyles().put("color", Color.RED);
        label.getStyles().put("horizontalAlignment",
            HorizontalAlignment.CENTER);
        label.getStyles().put("verticalAlignment",
            VerticalAlignment.CENTER);

        window.setContent(b);
        window.setTitle("Hello World!");
        window.setMaximized(true);

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }
}