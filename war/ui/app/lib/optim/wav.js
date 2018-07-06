function convert_wav(audioData, audioContext, newSampleRate) {
	var samples = mergeBuffers(audioData);
	samples = resample(samples, audioContext.sampleRate, newSampleRate);
	var buffer = encodeWAV(samples, newSampleRate);

	// return new Uint8Array(buffer)
	return buffer;
}

function mergeBuffers(audioData) {
	var sampleLength = 0;
	for (var i = 0; i < audioData.length; i++) {
		sampleLength += audioData[i].length;
	}
	var samples = new Float32Array(sampleLength);
	var sampleIdx = 0;
	for (var i = 0; i < audioData.length; i++) {
		for (var j = 0; j < audioData[i].length; j++) {
			samples[sampleIdx] = audioData[i][j];
			sampleIdx++;
		}
	}
	return samples;
}

function resample(samples, oldRate, newRate) {
	const newSamples = new Float32Array(Math.floor(samples.length * (newRate/oldRate)));

	for (let i=0; i<newSamples.length; i++) {
			const startPos = (oldRate/newRate) * i;
			const endPos = (oldRate/newRate) * (i + 1);
			const startIdx = Math.floor(startPos);
			const endIdx = Math.floor(endPos);
			const startMod = (startPos == startIdx) ? 1 : (1 - startPos % 1);
			const endMod = (endPos == endIdx) ? 0 : (endPos % 1);

			let sum = (samples[startIdx] || 0) * startMod + (samples[endIdx] || 0) * endMod;
			for (let i=startIdx+1; i<endIdx; i++) {
					sum += samples[i] || 0;
			}
			newSamples[i] = sum / (endPos - startPos);
	}
	return newSamples;
}

function encodeWAV(samples, sampleRate) {
	// var buffer = new ArrayBuffer(44 + samples.length * 2);
	// var view = new DataView(buffer);

	// this.writeString(view, 0, 'RIFF');	// RIFFヘッダ
	// view.setUint32(4, 32 + samples.length * 2, true); // これ以降のファイルサイズ
	// this.writeString(view, 8, 'WAVE'); // WAVEヘッダ
	// this.writeString(view, 12, 'fmt '); // fmtチャンク
	// view.setUint32(16, 16, true); // fmtチャンクのバイト数
	// view.setUint16(20, 1, true); // フォーマットID
	// view.setUint16(22, 1, true); // チャンネル数
	// view.setUint32(24, sampleRate, true); // サンプリングレート
	// view.setUint32(28, sampleRate * 2, true); // データ速度
	// view.setUint16(32, 2, true); // ブロックサイズ
	// view.setUint16(34, 16, true); // サンプルあたりのビット数
	// this.writeString(view, 36, 'data'); // dataチャンク
	// view.setUint32(40, samples.length * 2, true); // 波形データのバイト数
	// this.floatTo16BitPCM(view, 44, samples); // 波形データ

	var buffer = new ArrayBuffer(samples.length * 2);
	var view = new DataView(buffer);
	floatTo16BitPCM(view,0, samples); // 波形データ

	return buffer;
}

function floatTo16BitPCM(output, offset, input) {
	for (var i = 0; i < input.length; i++, offset += 2){
		var s = Math.max(-1, Math.min(1, input[i]));
		output.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
	}
}

//function writeString(view, offset, string) {
//		for (var i = 0; i < string.length; i++){
//			view.setUint8(offset + i, string.charCodeAt(i));
//		}
//}
