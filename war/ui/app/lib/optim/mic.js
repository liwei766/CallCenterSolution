class Mic {
  constructor(buffersize) {
    this.source = null;
		this.is_recoding = false;
		this.buffersize = buffersize;
  }
  
  start(func){
  	if(this.is_recoding) return;
  	
  	navigator.getMedia = navigator.getUserMedia ||
  			navigator.webkitGetUserMedia ||
  			navigator.mozGetUserMedia ||
  			navigator.msGetUserMedia;
  	
  	navigator.getMedia ({ audio:true }, (stream) => {
  		var context = new AudioContext();
  		var source = context.createMediaStreamSource(stream);
  		this.source = source;
  		var audioBufferArray = [];
  		var scriptProcessor = context.createScriptProcessor(this.buffersize, 1, 1);
  		source.connect(scriptProcessor);
  		// source.connect(context.destination);
  		scriptProcessor.connect(context.destination);
  		scriptProcessor.onaudioprocess = (event) => {
  				var channel = event.inputBuffer.getChannelData(0);
  				var buffer = new Float32Array(this.buffersize);
  				for (var i = 0; i < this.buffersize; i++) {
  						buffer[i] = channel[i];
  				}
  				audioBufferArray.push(buffer)
  				if(audioBufferArray.length >= 1){
  						var newSampleRate = 16000;
  						var data = convert_wav(audioBufferArray, context, newSampleRate);
  						// this.UTF8toBinary(this.header).then((utf) => {
  						//		this.ws.send(this.Combine(utf, data))
  						// })
//  						var blob = new Blob([this._createHeader(),data]);
  						func(data, data.byteLength / (newSampleRate * 2));
  						audioBufferArray = [];
  				}													
  		}
  		this.is_recoding = true;
  	}, function(err){ //エラー処理
  		func( false, err );
  	})
  }
  
  stop(){
  	if(!this.is_recoding) return;
  	
  	this.source.context.close();
		this.is_recoding = false;
  }
  
//  _createHeader(init) {
//    let header = {};
//    header.path = "audio";
//    header.order  = this.orderCount++;
//    if(init){
//        header.contentType = "audio/l16;rate=16000;channels=1";
//    } else {
//        header.sessionId = this.session_id;
//    }
//    return JSON.stringify(header) + "\r\n\r\n";
//  }
}

	