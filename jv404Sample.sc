JV404Sample {
  var <>b;
  var plotter;
  var w;
  var keyDownAction;
  var <> startPos = 0;
  var <> endPos = 1.0;
  var overlay;
  var selectionIndex = 0; // startPos or endPos on click;
  var bRecording = false;
  var recordStartTime = -1;
  var recorder;
  var parent;
  var id;
  var bank;
  var <> synth;
  *new{
		|buffer, parent_, id_, bank_|
		^super.new.init(buffer, parent_, id_, bank_);
	}
  init{
    |buffer, parent_, id_, bank_|
    b = buffer;
    id = id_;
    bank = bank_;
    parent = parent_;
    keyDownAction = {
      |doc, char, mod, unicode, keycode, key|
      key.postln;
      switch(key, 
      32, {
        startPos.postln;
        // Synth(\jv404, [amp: 1, buf: b, startPos: startPos, endPos: endPos]);
        if(synth != nil, {
          if(synth.isPlaying(), {
            synth.run(false);
          }, {
            synth.run();
            synth.set(\replay, 1);
          });
        });
      },
      49, {
        selectionIndex = 0;
      },
      50, {
        selectionIndex = 1;
      },
      82, {
        if(bRecording, {
          var recordEndTime = Date.getDate.rawSeconds;
          var delta = recordEndTime - recordStartTime;
          if(delta < 0.1, {
            "Recording too short, not stopping".postln;
          }, {
            var numSamples;
            var c = CondVar();
            var sampleRate = Server.local.sampleRate;
            ("Stop recording, dur: " ++ delta).postln;
            recorder.free;
            b.free; 
            delta.postln;
            numSamples = (Server.local.sampleRate * delta);
            b = Buffer.alloc(Server.local, numSamples, 1, completionMessage: { 
              c.signalOne
            }); 
            fork{
              c.wait({b.numFrames != nil});
              parent.recordBuffer.copyData(b, numSamples: b.numFrames); // !?
              startPos = 0; 
              endPos = 1;
              if(synth == nil, {
                synth = Synth.newPaused(\jv404, [doneAction: 1, amp: 1, buf: b, startPos: startPos, endPos: endPos]);
              });
            };
            bRecording = bRecording.not;
            this.bufferToPlotter(plotter);
            // Update view
          });
        },{
          "Start recording".postln;
          // Autoconnect Firefox?
          recordStartTime = Date.getDate.rawSeconds;
          recorder = Synth(\jv404Recorder, [buffer: parent.recordBuffer, in: 0]);
          bRecording = bRecording.not;
        });
      },
      83, {
        "Save: render to file".postln;
        this.render();
      }
    );
    }
  }
  bufferToPlotter{
    |plotter|
    var loadAction;
    loadAction = {
      |array, buf, test|
      {
        plotter.setValue(
          array.unlace(buf.numChannels),
          findSpecs: true,
          refresh: false,
          separately: false,
          minval: -1 ,
          maxval:1 
        );
        plotter.domainSpecs = ControlSpec(0.0, buf.numFrames, units: "");

        plotter.axisLabelX_(nil);
        plotter.plots[0].gridOnX_(false);
        // plotter.plots[0].gridOnY_(false);
        // plotter.plots[0].plotBounds.left = plotter.plots[0].bounds.left;
        // plotter.plots[0].plotBounds.top = plotter.plots[0].bounds.top;
        // plotter.plots[0].plotBounds.height = plotter.plots[0].bounds.height;
        // plotter.plots[0].plotBounds.width = plotter.plots[0].bounds.width;
        plotter.plots[0].backgroundColor_(Color.black);
        overlay.bounds = plotter.plots[0].plotBounds;
      }.defer
    };
    if(b != nil, {
      b.loadToFloatArray(action:loadAction);
    })
  }
  gui{
    if(b != nil, {
      synth = Synth.newPaused(\jv404, [doneAction: 1, amp: 1, buf: b, startPos: startPos, endPos: endPos]);
    });
    w = Window.new(this.getPathName(), Rect(100, 600,400, 100));
    w.view.keyDownAction = keyDownAction;
    plotter = Plotter.new("buffer", Rect(0,0,w.bounds.width, 100), w);

    plotter.showUnits_(false);
    plotter.plotColor_(Color.white);
    plotter.plotMode_(\filled);
    plotter.plotMode_(\lines);
    // plotter.setProperties(\labelX, "x", \backgroundColor, Color.black);
    // plotter.setGridProperties(\x, )
    // plotter.plotMode_(\stems);
    this.bufferToPlotter(plotter);
    overlay = UserView(w, plotter.bounds);
    // overlay.background_(Color(1.0, 1.0, 0, 0.5));
    overlay.mouseDownAction = {
      |view, x, y| 
      var pos = x / overlay.bounds.width;
      x.postln;
      pos.postln;
      if(selectionIndex == 0, {
        startPos = pos;
        if(synth != nil, { synth.set(\startPos, startPos); })
      }, {
        endPos = pos;
        if(synth != nil, { synth.set(\endPos, endPos); })
      });
      overlay.refresh;
    };

    overlay.drawFunc = {
      Pen.color = Color.white;
      Pen.moveTo(Point((startPos * overlay.bounds.width), 0));
      Pen.lineTo(Point((startPos * overlay.bounds.width), w.bounds.height));
      Pen.moveTo(Point(endPos * overlay.bounds.width, 0));
      Pen.lineTo(Point(endPos * overlay.bounds.width, w.bounds.height));
      Pen.stroke;
      Pen.color_(Color.white.alpha_(0.2));
      Pen.addRect(Rect.newSides(startPos * overlay.bounds.width, 0, endPos * overlay.bounds.width, w.bounds.height));
      Pen.perform(\fill);
    };
    overlay.refresh;
    w.onClose_({synth.free; parent.saveSettings()});
    w.front;
  }
  getPathName{
    ^(parent.dir.fullPath ++ "/" ++ (bank + 65).asAscii.asString ++ id.asString ++ ".wav");
  }
  render{
    var pathName = this.getPathName;
    // Based on length (start- endPoint) save to file
    // pathName.postln;
    b.write(pathName, "wav", "int24", numFrames: b.numFrames * (1.0 - startPos - (1.0 - endPos)), startFrame: b.numFrames * startPos);
    b = Buffer.read(Server.local, pathName);
    startPos = 0;
    endPos = 1;
    "Rendered file".postln;
    this.bufferToPlotter(plotter);
  }
}
