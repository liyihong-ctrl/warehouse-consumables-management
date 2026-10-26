package frame.server.service.impl;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import frame.server.service.MailService;

@Service("mailService")
public class MailServiceImpl implements MailService {
	
	private final String from = System.getenv().getOrDefault("MAIL_FROM", "noreply@example.invalid");

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendSimpleMail(String to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();

		// 收信人
		message.setTo(to);

		// 主题
		message.setSubject(subject);

		// 内容
		message.setText(content);

		// 发信人
		message.setFrom(from);

		mailSender.send(message);
	}

	@Override
	public void sendHtmlMail(String to, String subject, String content) {
		// 使用MimeMessage，MIME协议
		MimeMessage message = mailSender.createMimeMessage();

		// MimeMessageHelper帮助设置更丰富的内容
		MimeMessageHelper helper;

		try {
			helper = new MimeMessageHelper(message, true);

			// 收信人
			helper.setTo(to);
			String cc = System.getenv().getOrDefault("MAIL_CC", "");
			if (!cc.isEmpty()) {
				helper.setCc(cc);
			}

			// 主题
			helper.setSubject(subject);

			// true代表支持html
			helper.setText(content, true);

			// 收信人
			helper.setFrom(from);

			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println(e);
		}
	}

	@Override
	public void sendAttachmentMail(String to, String subject, String content, String filePath) {
		// 使用MimeMessage，MIME协议
		MimeMessage message = mailSender.createMimeMessage();

		// MimeMessageHelper帮助设置更丰富的内容
		MimeMessageHelper helper;

		try {
			// true代表支持多组件，如附件，图片等
			helper = new MimeMessageHelper(message, true);

			// 收信人
			helper.setTo(to);

			// 主题
			helper.setSubject(subject);

			// true代表支持html
			helper.setText(content, true);

			// 添加附件，可多次调用该方法添加多个附件
			FileSystemResource file = new FileSystemResource(new File(filePath));
			String fileName = file.getFilename();
			helper.addAttachment(fileName, file);

			// 收信人
			helper.setFrom(from);

			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println(e);
		}
	}

	@Override
	public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
		// 使用MimeMessage，MIME协议
		MimeMessage message = mailSender.createMimeMessage();

		// MimeMessageHelper帮助设置更丰富的内容
		MimeMessageHelper helper;
		try {
			// true代表支持多组件，如附件，图片等
			helper = new MimeMessageHelper(message, true);

			// 收信人
			helper.setTo(to);

			// 主题
			helper.setSubject(subject);

			// true代表支持html
			helper.setText(content, true);

			// 重复使用添加多个图片
			FileSystemResource res = new FileSystemResource(new File(rscPath));
			helper.addInline(rscId, res);

			// 收信人
			helper.setFrom(from);

			mailSender.send(message);
		} catch (MessagingException e) {
			System.out.println(e);
		}
	}

}
