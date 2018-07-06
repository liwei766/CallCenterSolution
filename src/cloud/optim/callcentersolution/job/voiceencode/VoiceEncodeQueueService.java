/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeQueueService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.job.voiceencode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.callcentersolution.api.entity.VoiceEncodeQueue;
import cloud.optim.callcentersolution.api.entity.dao.VoiceEncodeQueueDao;
import cloud.optim.callcentersolution.core.modules.ffmpeg.FFmpeg;


/**
 * VoiceEncodeQueueService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class VoiceEncodeQueueService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/**
	 * FFmpeg
	 */
	@Resource
	private FFmpeg ffmpeg;

	/**
	 * HibernateDAO
	 */
	@Resource
	private VoiceEncodeQueueDao voiceEncodeQueueDao;

	/**
	 * VoiceEncodeQueueDao 取得
	 * @return VoiceEncodeQueueDao
	 */
	public VoiceEncodeQueueDao getVoiceEncodeQueueDao() {
		return voiceEncodeQueueDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private VoiceEncodeQueueMapper voiceEncodeQueueMapper;

	/**
	 * VoiceEncodeQueueMapper 取得
	 * @return VoiceEncodeQueueMapper
	 */
	public VoiceEncodeQueueMapper getVoiceEncodeQueueMapper() {
		return voiceEncodeQueueMapper;
	}

	// -------------------------------------------------------------------------

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${call.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 分割音声ファイル名 */
	@Value( "${call.voice.file.name}" )
	private String voiceFileName;

	/** 圧縮時の一時ファイル名 */
	@Value( "${encode.output.tmp.file.name}" )
	private String encodeTmpFileName;

	/** 圧縮音声ファイル名 */
	@Value( "${encode.output.file.name}" )
	private String encodedFileName;

	// -------------------------------------------------------------------------

	/**
	 * 圧縮対象データ検索
	 * @param maxResult 取得件数上限
	 * @param offset オフセット
	 * @return 圧縮対象データリスト
	 * @throws Exception エラー
	 */
	public List<VoiceEncodeQueue> search(Integer maxResult, Integer offset) throws Exception {
		List<VoiceEncodeQueue> list = voiceEncodeQueueMapper.search(maxResult, offset);
		return list;
	}

	/**
	 * エンコード後にキューからデータを削除
	 * @param id エンティティの識別ID
	 * @throws Exception
	 */
	public void encode( VoiceEncodeQueue entity ) throws Exception {
		// キューからデータを削除する
		this.voiceEncodeQueueDao.delete( entity );

		// 音声ファイル名の取得
		String dirPath  = Paths.get(voiceFileRootDirectory, entity.getCompanyId(), entity.getCallLogId().toString()).toString();
		Path inputFile = Paths.get(dirPath, String.format(voiceFileName, entity.getCallLogDetailId()));
		Path tmpOutputFile = Paths.get(dirPath, String.format(encodeTmpFileName, entity.getCallLogDetailId()));

		// ffmpegでwavファイルを圧縮する
		ffmpeg.encode(inputFile, tmpOutputFile);

		// 圧縮ファイルのファイル名を変更する
		if(!tmpOutputFile.toFile().renameTo(Paths.get(dirPath, String.format(encodedFileName, entity.getCallLogDetailId())).toFile())) {
			throw new Exception("ファイルのリネームに失敗しました。");
		}

		// wavファイルを削除する
		inputFile.toFile().delete();
	}
}