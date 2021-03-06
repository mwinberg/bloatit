//
// Copyright (c) 2011 Linkeos.
//
// This file is part of Elveos.org.
// Elveos.org is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.
//
// Elveos.org is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details.
// You should have received a copy of the GNU General Public License along
// with Elveos.org. If not, see http://www.gnu.org/licenses/.
//
package com.bloatit.framework.mailsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.bloatit.common.Log;
import com.bloatit.framework.FrameworkConfiguration;
import com.bloatit.framework.exceptions.highlevel.BadProgrammerException;

/**
 * <p>
 * A thread in charges of asynchronous mail sending.
 * </p>
 * <p>
 * <b>Note:</b> a file in the user configuration directory (default /home/
 * {@code<user>} /.config/bloatit/) called mail.properties should exist with the
 * following values
 * 
 * <pre>
 * #mail.smtp.host=smtp.gmail.com (example value with gmail)
 * mail.smtp.host=
 * mail.smtp.socketFactory.port=465
 * mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
 * mail.smtp.auth=true
 * mail.smtp.port=465
 * #mail.from=example@gmail.com
 * #mail.login=example@gmail.com
 * #mail.password={@code<example login>}
 * mail.from=
 * mail.login=
 * mail.password=
 * </pre>
 * 
 * </p>
 */
public class MailServer extends Thread {
    private final static String WIP_MAIL_DIRECTORY = FrameworkConfiguration.getMailDirTmp();
    private final static String SENT_MAIL_DIRECTORY = FrameworkConfiguration.getMailDirSend();
    private final static String FLUSH_AND_STOP = "FLUSHANDSTOP";

    private Session session;
    private LinkedBlockingQueue<String> mailsFileName;

    private AtomicBoolean stop;

    private static volatile MailServer instance;

    private MailServer() {
        // disactivating CTOR
    }

    /**
     * <p>
     * Initializes the mail server
     * </p>
     * <p>
     * This method has to be called before you start application
     * </p>
     */
    public void initialize() {
        mailsFileName = new LinkedBlockingQueue<String>();
        stop = new AtomicBoolean(false);

        handleUnsent();
        createSentDirectory();
        createWipDirectory();

        final Properties prop = new Properties();
        prop.put("mail.smtp.host", FrameworkConfiguration.getMailSmtpHost());
        prop.put("mail.smtp.socketFactory.port", FrameworkConfiguration.getMailSmptSocketFactoryPort());
        prop.put("mail.smtp.socketFactory.class", FrameworkConfiguration.getMailSmtpSoketFactoryClass());
        prop.put("mail.smtp.auth", FrameworkConfiguration.getMailSmtpAuth());
        prop.put("mail.smtp.port", FrameworkConfiguration.getMailSmtpPort());

        session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FrameworkConfiguration.getMailLogin(), FrameworkConfiguration.getMailPassword());
            }
        });
    }

    /**
     * <p>
     * Finds the current instance of the mail server.
     * </p>
     * <p>
     * If no instance exists yet, this method will not create one, instead it
     * will throw an exception. Therefore, init should always be called before
     * </p>
     * 
     * @return the MailServer instance
     * @throws MailFatalError when the mail server cannot be created (i.e. :
     *             directories to store mails can't be created)
     */
    public synchronized static MailServer getInstance() {
        if (instance == null) {
            instance = new MailServer();
        }
        return instance;
    }

    /**
     * <p>
     * Sends a new mail via the mail server
     * </p>
     * <p>
     * This method call will create a temporary file holding the content of the
     * "to be sent mail", then return. Another thread will later be in charge of
     * handling those created files and to send mails. <br/>
     * Hence, execution time of this method is close to the creation of a UUID
     * plus creation of a file <br />
     * In case of a crash during this method, it is possible that the file is
     * not created yet, therefore resulting in a loss of an email. It should
     * therefore be called before doing any other actions (such as informing the
     * user that the mail has been sent, or updating the database).
     * </p>
     * 
     * @param mail the mail to send
     */
    public void send(final Mail mail) {
        if (mail == null) {
            throw new IllegalArgumentException("mail shouldn't be null");
        }

        try {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FrameworkConfiguration.getMailFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo()));
            final Address[] replyTo = { new InternetAddress(FrameworkConfiguration.getMailReplyTo()) };
            message.setReplyTo(replyTo);
            message.setSubject(mail.getSubject());

            if (mail.hasAttachment()) {
                final MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(mail.getContent(), mail.getMimeType());
                final Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);

                // Attachment
                final MimeBodyPart attachmentPart = new MimeBodyPart();
                final DataSource source = new FileDataSource(mail.getAttachment());
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(mail.getAttachmentName());

                attachmentPart.setHeader("Content-Type", source.getContentType());
                attachmentPart.setHeader("Content-ID", mail.getAttachmentName());
                attachmentPart.setDisposition(Part.ATTACHMENT);

                multipart.addBodyPart(attachmentPart);

                // Put parts in message
                message.setContent(multipart);
            } else {
                message.setContent(mail.getContent(), mail.getMimeType());
            }

            final UUID uuid = UUID.randomUUID();
            final String fileName = uuid.toString() + "-" + mail.getMailSenderID();
            final File mailOutput = new File(WIP_MAIL_DIRECTORY + '/' + fileName);

            // Save the mail into the temporary mail directory
            createWipDirectory();
            if (!mailOutput.createNewFile()) {
                throw new BadProgrammerException("Couldn't create file " + mailOutput.getCanonicalPath());
            }
            final FileOutputStream mailWriter = new FileOutputStream(mailOutput);

            // Add the mail in the 'to send' list
            message.writeTo(mailWriter);
            mailsFileName.add(fileName);
        } catch (final AddressException e) {
            Log.mail().fatal("Error parsing the message. Check your configuration file", e);
        } catch (final MessagingException e) {
            Log.mail().fatal("Error composing the new mail. Check your configuration file", e);
        } catch (final FileNotFoundException e) {
            throw new MailFatalError("I cannot write to " + WIP_MAIL_DIRECTORY + '/' + " check that I have correct user rights", e);
        } catch (final IOException e) {
            throw new MailFatalError("I cannot write to " + WIP_MAIL_DIRECTORY + '/' + " check that I have correct user rights", e);
        }
    }

    /**
     * <p>
     * Waits till there is no more message in the queue then ends the server.
     * </p>
     * <p>
     * If messages are added after the call to flushAndStop, these messages
     * won't be handled.
     * </p>
     * <p>
     * If any sending error happens after the call to flushAndStop, no retry
     * will be performed for these messages. Server should try to send them on
     * next startup.
     * </p>
     * <p>
     * Note : Using a poison pill method to shutdown
     * </p>
     */
    public void flushAndStop() {
        Log.mail().trace("Requested to stop whenever I finished flushing messages. All messages past this time will be ignored");
        mailsFileName.add(FLUSH_AND_STOP);
    }

    /**
     * <p>
     * Stops the server after he finished sending the current message. If there
     * is no message in the queue, instantly stops the server.
     * </p>
     */
    public void quickStop() {
        Log.mail().trace("Requested to stop now");
        stop.set(true);
        getInstance().interrupt();
    }

    /**
     * <p>
     * Should be private, do not call it
     * </p>
     */
    @Override
    public void run() {
        while (true) {
            String mailFileName = "";
            try {
                mailFileName = mailsFileName.take();

                if (mailFileName.equals(FLUSH_AND_STOP)) {
                    Log.mail().info("Shutting down after flushing messages. " + mailsFileName.size() + " message not handled");
                    return;
                }

                final File mailFile = new File(WIP_MAIL_DIRECTORY + '/' + mailFileName);

                FileInputStream mailRead;
                mailRead = new FileInputStream(mailFile);

                final File outputFile = new File(SENT_MAIL_DIRECTORY + '/' + mailFileName);

                final Message message = new MimeMessage(session, mailRead);
                Transport.send(message);

                if (!mailFile.renameTo(outputFile)) {
                    throw new BadProgrammerException("Couldn't move sent mail");
                }
                Log.mail().trace("Mail sent " + mailFileName);
                resetRetries();
            } catch (final InterruptedException e) {
                // Happens when waiting on the queue
                if (stop.get()) {
                    Log.mail().info("Shutting down NOW. " + mailsFileName.size() + " messages not handled");
                    return;
                }
                Log.mail().error("Received an interruption without being asked to shutdown. Ignoring.", e);
            } catch (final MessagingException e) {
                Log.mail().error("Error when sending mail " + mailFileName);
                // Happens when trying to send the mail
                final long waitTime = timeToRetry();
                if (waitTime != -1) {
                    Log.mail().fatal("Failed to send mail " + mailFileName + " retrying in : " + waitTime, e);
                    mailsFileName.add(mailFileName);
                    try {
                        sleep(waitTime);
                    } catch (final InterruptedException e1) {
                        Log.mail().info("Unexpectedly interrupted in the middle of the retry policy. Ignoring and going on");
                    }
                } else {
                    Log.mail().info("Reached end of retry policy.");
                }
            } catch (final FileNotFoundException e) {
                /*
                 * Happens when trying to read the mail from the file Shouldn't
                 * happen, the file creation happened before we are notified. If
                 * it happen this is not good. We don't try to resend as most
                 * probably it will happen again later.
                 */
                Log.mail().fatal("Couldn't read the file containing mail " + mailFileName, e);
            }
        }
    }

    /**
     * Call whenever we manage to send a message.
     */
    private final void resetRetries() {
        FrameworkConfiguration.getMailRetryPolicy().reinit();
    }

    /**
     * Computes the time to wait before we do a retry.
     * 
     * @return The time in milliseconds before a retry to send a mail
     */
    private final long timeToRetry() {
        return FrameworkConfiguration.getMailRetryPolicy().getNext();
    }

    /**
     * Make sure there is a directory to store not sent mails
     */
    private final void createWipDirectory() {
        final File wipDir = new File(WIP_MAIL_DIRECTORY);
        if (!wipDir.exists()) {
            if (!wipDir.mkdirs()) {
                throw new BadProgrammerException("Couldn't create directory " + SENT_MAIL_DIRECTORY);
            }

            Log.mail().info("Created directory " + WIP_MAIL_DIRECTORY);
        }
    }

    /**
     * Make sure there is a directory to store sent mails
     */
    private final void createSentDirectory() {
        final File sentDir = new File(SENT_MAIL_DIRECTORY);
        if (!sentDir.exists()) {
            if (!sentDir.mkdirs()) {
                throw new BadProgrammerException("Couldn't create directory " + SENT_MAIL_DIRECTORY);
            }
            Log.mail().info("Created directory " + SENT_MAIL_DIRECTORY);
        }
    }

    /**
     * <p>
     * A method that will prepare all unsent messages (aka message in the
     * temporary file).
     * </p>
     * <p>
     * This method should be called in the constructor
     * </p>
     */
    private final void handleUnsent() {
        final File wipDir = new File(WIP_MAIL_DIRECTORY);
        if (!wipDir.exists() || !wipDir.isDirectory()) {
            return;
        }
        final File[] children = wipDir.listFiles();
        for (final File child : children) {
            mailsFileName.add(child.getName());
        }
    }
}
