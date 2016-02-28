"""empty message

Revision ID: 51635ba5d0d9
Revises: 3be461541e86
Create Date: 2016-02-28 20:43:47.877626

"""

# revision identifiers, used by Alembic.
revision = '51635ba5d0d9'
down_revision = '3be461541e86'

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

def upgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('user', 'password')
    ### end Alembic commands ###


def downgrade():
    ### commands auto generated by Alembic - please adjust! ###
    op.add_column('user', sa.Column('password', mysql.VARCHAR(length=120), nullable=True))
    ### end Alembic commands ###
