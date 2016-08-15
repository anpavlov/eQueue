# coding=utf-8
import smtplib

FROM = 'equeue@list.ru'
welcome_msg = "\r\n".join(["From: Команда eQueue <equeue@list.ru>",
                           "To: {}",
                           "Subject: Добро пожаловать в eQueue!",
                           "",
                           "Дорогой пользователь,\n\nДобро пожаловать в eQueue!\nПо любому вопросу пишите на equeue@list.ru\n\nС уважением,\nкоманда eQueue"])

server = smtplib.SMTP('smtp.mail.ru:587')
server.ehlo()
server.starttls()
server.login('equeue@list.ru', ",./l;'p[]")


def send_welcome_email(to):
    server.sendmail(FROM, to, welcome_msg.format(to))


if __name__ == '__main__':
    send_welcome_email('digwnews@gmail.com')
