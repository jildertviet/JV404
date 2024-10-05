b = Buffer.read(s, "/home/jildert/Music/404bank0/A0.wav")
b.plot
(
var action;
var plotter;
w = Window.new(PathName.new(b.path).fileName, Rect(100, 600,400, 100));
plotter = Plotter.new("buffer", Rect(0,0,w.bounds.width, 100), w);

action = { |array, buf|
  var unitStr = if (buf.numChannels == 1) { "" } { "" };
  {
    plotter.setValue(
      array.unlace(buf.numChannels),
      findSpecs: true,
      refresh: false,
      separately: false,
      minval: -1 ,
      maxval:1 
    );
    plotter.domainSpecs = ControlSpec(0.0, buf.numFrames, units: unitStr);
    // save vertical space with highly multichannel buffer plots
plotter.axisLabelX_(nil);
plotter.plots[0].gridOnX_(false);
plotter.plots[0].gridOnY_(false);
plotter.plots[0].plotBounds.left = plotter.plots[0].bounds.left;
plotter.plots[0].plotBounds.top = plotter.plots[0].bounds.top;
plotter.plots[0].plotBounds.height = plotter.plots[0].bounds.height;
plotter.plots[0].plotBounds.width = plotter.plots[0].bounds.width;
plotter.plots[0].backgroundColor_(Color.black);
// plotter.plots[0].showUnits_(false);
// plotter.plots[0].hideLabelsHeightRatio_(1.0);
// plotter.plots[0].drawGrid_(false);
  }.defer
};
plotter.showUnits_(false);
plotter.plotColor_(Color.white);
// plotter.plotMode_(\filled);
// plotter.setProperties(\labelX, "x", \backgroundColor, Color.black);
// plotter.setGridProperties(\x, )
plotter.plotMode_(\stems);
b.loadToFloatArray(action:action);
w.front;
a = plotter;
)

a.plots[0].plotBounds_(Rect(0, 0, 100, 100));
a.plots[0].inspect
Plot


x = b.plot
w = x.parent;
w.view
Pen
Window
View
UserView
u = UserView(w, Rect(0, 0, w.bounds.width, w.bounds.height));
u.background_(Color(1.0, 1.0, 0, 0.5));
u.mouseDownAction = {|view, x, y| var pos = x / w.bounds.width; pos.postln};
(
var startPoint = 0.0;
var endPoint = 0.75;
u.drawFunc = {
  Pen.color = Color.red;
  Pen.moveTo(Point(startPoint, 0));
  Pen.lineTo(Point(startPoint, w.bounds.height));
  Pen.moveTo(Point(endPoint * w.bounds.width, 0));
  Pen.lineTo(Point(endPoint * w.bounds.width, w.bounds.height));
  Pen.stroke;
};
u.refresh;
)
w.onmouse
