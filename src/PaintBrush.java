import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PaintBrush extends JFrame {
    private JButton lineButton, rectButton, ovalButton, pencilButton, eraserButton;
    private Color[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};
    private JToggleButton solidButton, dottedButton;
    private JButton clearButton, undoButton;
    private DrawArea drawArea;

    private Color currentColor = Color.BLACK;
    private String currentShape = "PENCIL";
    private boolean isDotted = false;

    public PaintBrush() {
        setTitle("Paint Brush");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        lineButton = new JButton("Line");
        rectButton = new JButton("Rect");
        ovalButton = new JButton("Oval");
        pencilButton = new JButton("Pencil");
        eraserButton = new JButton("Eraser");
        controlPanel.add(lineButton);
        controlPanel.add(rectButton);
        controlPanel.add(ovalButton);
        controlPanel.add(pencilButton);
        controlPanel.add(eraserButton);

        for (Color color : colors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(20, 20));
            controlPanel.add(colorButton);
            colorButton.addActionListener(e -> currentColor = color);
        }

        solidButton = new JToggleButton("Solid", true);
        dottedButton = new JToggleButton("Dotted");
        ButtonGroup group = new ButtonGroup();
        group.add(solidButton);
        group.add(dottedButton);
        controlPanel.add(solidButton);
        controlPanel.add(dottedButton);

        clearButton = new JButton("Clear");
        undoButton = new JButton("Undo");
        controlPanel.add(clearButton);
        controlPanel.add(undoButton);

        add(controlPanel, BorderLayout.NORTH);

        drawArea = new DrawArea();
        add(new JScrollPane(drawArea), BorderLayout.CENTER);

        lineButton.addActionListener(e -> currentShape = "LINE");
        rectButton.addActionListener(e -> currentShape = "RECT");
        ovalButton.addActionListener(e -> currentShape = "OVAL");
        pencilButton.addActionListener(e -> currentShape = "PENCIL");
        eraserButton.addActionListener(e -> currentShape = "ERASER");
        solidButton.addActionListener(e -> isDotted = false);
        dottedButton.addActionListener(e -> isDotted = true);
        clearButton.addActionListener(e -> drawArea.clear());
        undoButton.addActionListener(e -> drawArea.undo());
    }

    private interface Shape {
        void draw(Graphics g);
        void update(int x2, int y2);
    }

    private class Line implements Shape {
        private int x1, y1, x2, y2;
        private Color color;
        private boolean isDotted;

        public Line(int x1, int y1, int x2, int y2, Color color, boolean isDotted) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.isDotted = isDotted;
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            if (isDotted) {
                float[] dash = {4f, 0f, 2f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f));
            } else {
                g2d.setStroke(new BasicStroke());
            }
            g2d.drawLine(x1, y1, x2, y2);
        }

        @Override
        public void update(int x2, int y2) {
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    private class RectangleShape implements Shape {
        private int x, y, width, height;
        private Color color;
        private boolean isDotted;

        public RectangleShape(int x, int y, int width, int height, Color color, boolean isDotted) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.isDotted = isDotted;
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            if (isDotted) {
                float[] dash = {4f, 0f, 2f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f));
            } else {
                g2d.setStroke(new BasicStroke());
            }
            g2d.drawRect(x, y, width, height);
        }

        @Override
        public void update(int x2, int y2) {
            this.width = Math.abs(x2 - x);
            this.height = Math.abs(y2 - y);
            this.x = Math.min(x, x2);
            this.y = Math.min(y, y2);
        }
    }

    private class OvalShape implements Shape {
        private int x, y, width, height;
        private Color color;
        private boolean isDotted;

        public OvalShape(int x, int y, int width, int height, Color color, boolean isDotted) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.isDotted = isDotted;
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            if (isDotted) {
                float[] dash = {4f, 0f, 2f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f));
            } else {
                g2d.setStroke(new BasicStroke());
            }
            g2d.drawOval(x, y, width, height);
        }

        @Override
        public void update(int x2, int y2) {
            this.width = Math.abs(x2 - x);
            this.height = Math.abs(y2 - y);
            this.x = Math.min(x, x2);
            this.y = Math.min(y, y2);
        }
    }

    private class Pencil implements Shape {
        private ArrayList<Point> points;
        private Color color;
        private boolean isDotted;

        public Pencil(int x, int y, Color color, boolean isDotted) {
            points = new ArrayList<>();
            points.add(new Point(x, y));
            this.color = color;
            this.isDotted = isDotted;
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            if (isDotted) {
                float[] dash = {4f, 0f, 2f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f));
            } else {
                g2d.setStroke(new BasicStroke());
            }
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        @Override
        public void update(int x2, int y2) {
            points.add(new Point(x2, y2));
        }
    }

    private class Eraser implements Shape {
        private ArrayList<Point> points;
        private boolean isDotted;

        public Eraser(int x, int y, boolean isDotted) {
            points = new ArrayList<>();
            points.add(new Point(x, y));
            this.isDotted = isDotted;
        }

        @Override
        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            if (isDotted) {
                float[] dash = {4f, 0f, 2f};
                g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 2f));
            } else {
                g2d.setStroke(new BasicStroke(8));
            }
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        @Override
        public void update(int x2, int y2) {
            points.add(new Point(x2, y2));
        }
    }

    private class DrawArea extends JPanel implements MouseListener, MouseMotionListener {
        private ArrayList<Shape> shapes = new ArrayList<>();
        private Shape currentShapeObj;

        public DrawArea() {
            setBackground(Color.WHITE);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Shape shape : shapes) {
                shape.draw(g);
            }
            if (currentShapeObj != null) {
                currentShapeObj.draw(g);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            switch (currentShape) {
                case "LINE":
                    currentShapeObj = new Line(e.getX(), e.getY(), e.getX(), e.getY(), currentColor, isDotted);
                    break;
                case "RECT":
                    currentShapeObj = new RectangleShape(e.getX(), e.getY(), 0, 0, currentColor, isDotted);
                    break;
                case "OVAL":
                    currentShapeObj = new OvalShape(e.getX(), e.getY(), 0, 0, currentColor, isDotted);
                    break;
                case "PENCIL":
                    currentShapeObj = new Pencil(e.getX(), e.getY(), currentColor, isDotted);
                    break;
                case "ERASER":
                    currentShapeObj = new Eraser(e.getX(), e.getY(), isDotted);
                    break;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            shapes.add(currentShapeObj);
            currentShapeObj = null;
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentShapeObj != null) {
                currentShapeObj.update(e.getX(), e.getY());
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        public void clear() {
            shapes.clear();
            repaint();
        }

        public void undo() {
            if (!shapes.isEmpty()) {
                shapes.remove(shapes.size() - 1);
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaintBrush app = new PaintBrush();
            app.setVisible(true);
        });
    }
}
