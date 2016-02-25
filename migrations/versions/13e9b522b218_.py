"""empty message

Revision ID: 13e9b522b218
Revises: 3d4c4af8cdf4
Create Date: 2016-02-22 18:58:53.981935

"""

# revision identifiers, used by Alembic.
revision = '13e9b522b218'
down_revision = '3d4c4af8cdf4'

from alembic import op
import sqlalchemy as sa


def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_index('email', table_name='user')
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.create_index('email', 'user', ['email'], unique=True)
    ### end Alembic commands ###
